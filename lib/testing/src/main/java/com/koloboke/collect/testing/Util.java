/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.koloboke.collect.testing;

import junit.framework.AssertionFailedError;

import java.lang.reflect.Method;


public final class Util {

    public static Method getTestMethod(Class testClass, String methodName) {
        try {
            return testClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Could not find method to suppress ", e);
        }
    }

    /**
     * Copy of {@link com.google.common.collect.testing.Helpers#fail(Throwable, Object)}
     */
    public static void fail(Throwable cause, Object message) {
        AssertionFailedError assertionFailedError =
                new AssertionFailedError(String.valueOf(message));
        assertionFailedError.initCause(cause);
        throw assertionFailedError;
    }

    private Util() {}
}
