/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.tools.agentscript.impl;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.tools.agentscript.FrameLibrary;
import com.oracle.truffle.tools.agentscript.FrameLibrary.Query;
import java.util.Set;

@SuppressWarnings("unused")
@ExportLibrary(value = FrameLibrary.class, receiverType = Query.class)
public final class DefaultFrameLibrary {
    @CompilerDirectives.TruffleBoundary
    @ExportMessage
    static Object readMember(
                    Query env,
                    String member) throws UnknownIdentifierException {
        return FrameLibrary.defaultReadMember(env, member);
    }

    @CompilerDirectives.TruffleBoundary
    @ExportMessage
    static void collectNames(Query env,
                    Set<String> names) throws InteropException {
        FrameLibrary.defaultCollectNames(env, names);
    }
}
