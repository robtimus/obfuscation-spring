/*
 * TestObfuscatorProvider.java
 * Copyright 2020 Rob Spoor
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

package com.github.robtimus.obfuscation.spring;

import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscatorProvider;

class TestObfuscatorProvider implements ObfuscatorProvider {

    static final Obfuscator OBFUSCATOR = Obfuscator.portion()
            .keepAtStart(2)
            .keepAtEnd(2)
            .withFixedTotalLength(8)
            .build();

    @Override
    public Obfuscator obfuscator() {
        return OBFUSCATOR;
    }
}
