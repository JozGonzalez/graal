/*
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.dsl.processor.interop;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.oracle.truffle.dsl.processor.ProcessorContext;
import com.oracle.truffle.dsl.processor.java.ElementUtils;

final class Utils {

    static String getReceiverTypeFullClassName(AnnotationMirror annotation) {
        TypeMirror mirror = ElementUtils.getAnnotationValue(TypeMirror.class, annotation, "receiverType");
        return ElementUtils.getQualifiedName(mirror);
    }

    static boolean getMessage(ProcessingEnvironment processingEnv, String messageName) {
        if (InteropDSLProcessor.KNOWN_MESSAGES.contains(messageName)) {
            return true;
        } else {
            TypeElement typeElement = ElementUtils.getTypeElement(processingEnv, messageName);
            ProcessorContext context = ProcessorContext.getInstance();
            if (typeElement != null && processingEnv.getTypeUtils().isAssignable(typeElement.asType(), context.getTypes().InteropMessage)) {
                return true;
            }
        }
        return false;
    }

    static boolean isObjectArray(TypeMirror actualType) {
        return actualType.getKind() == TypeKind.ARRAY && ElementUtils.getQualifiedName(actualType).equals("java.lang.Object");
    }

    static String getFullResolveClassName(TypeElement innerClass) {
        return ElementUtils.getPackageName(innerClass) + "." + getSimpleResolveClassName(innerClass);
    }

    static String getSimpleResolveClassName(TypeElement innerClass) {
        String generatedClassName = ElementUtils.getSimpleName(innerClass);
        if (generatedClassName.endsWith("Node")) {
            generatedClassName = generatedClassName.substring(0, generatedClassName.length() - "Node".length());
        }
        return generatedClassName + "SubNode";
    }

    static void appendFactoryGeneratedFor(Writer w, String indent, String generatedFor, String generatedBy) throws IOException {
        w.append("\n");
        w.append(indent).append("/**\n");
        w.append(indent).append(" * This foreign access factory is generated by {@link ").append(generatedBy).append("}.\n");
        w.append(indent).append(" * You are supposed to use it for the receiver object {@link ").append(generatedFor).append("}.\n");
        w.append(indent).append(" */\n");
        w.append(indent).append("@GeneratedBy(").append(generatedBy).append(".class)\n");
    }

    static void suppressDeprecationWarnings(Writer w, String indent) throws IOException {
        w.append("\n");
        w.append(indent).append("@SuppressWarnings(\"deprecation\")");
        w.append("\n");
    }

    static void appendMessagesGeneratedByInformation(Writer w, String indent, String generatedBy, String generatedFor) throws IOException {
        w.append("\n");
        w.append(indent).append("/**\n");
        w.append(indent).append(" * This message resolution is generated by {@link ").append(generatedBy).append("}.\n");
        if (generatedFor != null) {
            w.append(indent).append(" * Generated for {@link ").append(generatedFor).append("}.\n");
        }
        w.append(indent).append(" */\n");
    }

    static void appendVisibilityModifier(Writer w, TypeElement element) throws IOException {
        if (element.getModifiers().contains(Modifier.PUBLIC)) {
            w.append("public ");
        } else if (element.getModifiers().contains(Modifier.PROTECTED)) {
            w.append("protected ");
        } else if (element.getModifiers().contains(Modifier.PRIVATE)) {
            // there is a check that the class is not private
            throw new IllegalStateException();
        }
    }

}
