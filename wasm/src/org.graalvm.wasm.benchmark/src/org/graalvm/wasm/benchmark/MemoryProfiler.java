/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
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
package org.graalvm.wasm.benchmark;

import org.graalvm.polyglot.Context;
import org.graalvm.wasm.utils.cases.WasmCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.graalvm.wasm.utils.cases.WasmCase.collectFileCase;

public class MemoryProfiler {
    private static int WARMUP_ITERATIONS = 10;
    private static int ITERATIONS = 10;

    public static void main(String[] paths) throws IOException, InterruptedException {
        for (final String path : paths) {
            final String[] pathParts = path.split("/");
            final String type = pathParts[0];
            final String resource = String.join("/", pathParts[1], pathParts[2]);
            final String caseSpec = String.join("/", Arrays.copyOfRange(pathParts, 3, pathParts.length));
            final WasmCase benchmarkCase = collectFileCase(type, resource, caseSpec);
            assert benchmarkCase != null : String.format("Test case %s/%s not found.", resource, caseSpec);

            final Context.Builder contextBuilder = Context.newBuilder("wasm");
            contextBuilder.option("wasm.Builtins", "testutil,env:emscripten,memory");

            final List<Double> results = new ArrayList<>();

            for (int i = 0; i < WARMUP_ITERATIONS + ITERATIONS; ++i) {
                final Context context = contextBuilder.build();

                final double heapSizeBefore = getHeapSize();

                // The code we want to profile:
                benchmarkCase.getSources().forEach(context::eval);

                final double heapSizeAfter = getHeapSize();
                final double result = heapSizeAfter - heapSizeBefore;
                if (i < WARMUP_ITERATIONS) {
                    System.out.format("%s: warmup_iteration[%d]: %.3f MB%n", caseSpec, i, result);
                } else {
                    results.add(result);
                    System.out.format("%s: iteration[%d]: %.3f MB%n", caseSpec, i - WARMUP_ITERATIONS, result);
                }

                context.close();
            }

            Collections.sort(results);

            System.out.format("%s: median: %.3f MB%n", caseSpec, median(results));
            System.out.format("%s: min: %.3f MB%n", caseSpec, results.get(results.size() - 1));
            System.out.format("%s: max: %.3f MB%n", caseSpec, results.get(0));
            System.out.format("%s: average: %.3f MB%n", caseSpec, average(results));
        }
    }

    private static double getHeapSize() throws InterruptedException {
        Thread.sleep(100);
        System.gc();
        Thread.sleep(500);
        final Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1000000.0;
    }

    private static double median(List<Double> xs) {
        final int size = xs.size();
        if (size % 2 == 0) {
            return (xs.get(size / 2) + xs.get(size / 2 - 1)) / 2.0;
        } else {
            return xs.get(size / 2);
        }
    }

    private static double average(List<Double> xs) {
        double result = 0.0;
        for (double x : xs) {
            result += x;
        }
        return result / xs.size();
    }
}
