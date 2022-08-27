/*
 * Copyright (c) 2022, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.core.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BooleanSupplier;

import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

/**
 * Convenience annotation to automatically register a singleton instance of the annotated class in
 * {@link ImageSingletons}.
 *
 * The singleton is allocated and registered in a {@link Feature#afterRegistration} hook, and the
 * feature does not have any dependencies on other features. The order in which such features are
 * registered is not deterministic and cannot be influenced. This means that the constructor of
 * annotated class must not rely on other features being registered already, or other image
 * singletons being present already.
 *
 * The requirements and restrictions of {@link AutomaticallyRegisteredFeature} apply also to this
 * annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Platforms(Platform.HOSTED_ONLY.class)
public @interface AutomaticallyRegisteredImageSingleton {

    /**
     * The keys under which the singleton is registered in {@link ImageSingletons}. If no keys are
     * specified, the annotated class itself is used as the key.
     */
    Class<?>[] value() default {};

    /**
     * Register only if all provided supplied booleans are true. If no supplier is registered, the
     * singleton is register unconditionally.
     */
    Class<? extends BooleanSupplier>[] onlyWith() default {};
}
