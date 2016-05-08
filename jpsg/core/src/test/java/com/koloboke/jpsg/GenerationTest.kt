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

package com.koloboke.jpsg

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class GenerationTest {

    @Test
    fun generationTest() {
        val generator = Generator()
        generator.init()
        Generator.setCurrentSourceFile(File("package-info.java"))
        val cxt = Context.builder().put("jdk", SimpleOption("JDK6")).makeContext()
        val result = generator.generate(source = cxt, target = cxt, template = "package-info.java")
        assertEquals("package-info.java", result)
    }
}


