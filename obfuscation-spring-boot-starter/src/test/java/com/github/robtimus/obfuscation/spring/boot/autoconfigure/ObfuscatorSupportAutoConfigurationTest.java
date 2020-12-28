/*
 * ObfuscatorSupportAutoConfigurationTest.java
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

package com.github.robtimus.obfuscation.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.stereotype.Component;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscateFixedValue;

@SuppressWarnings("nls")
class ObfuscatorSupportAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ObfuscatorSupportAutoConfiguration.class, BeanWithObfuscators.class));

    @Test
    @DisplayName("enabled by default with out-of-the-box default obfuscator")
    void testEnabledByDefault() {
        contextRunner.run(context -> {
            BeanWithObfuscators bean = context.getBean(BeanWithObfuscators.class);

            assertThat(bean.defaultObfuscator).isEqualTo(Obfuscator.fixedLength(3));
            assertThat(bean.annotatedObfuscator).isEqualTo(Obfuscator.fixedValue("<fixed>"));
        });
    }

    @Test
    @DisplayName("enabled by default with configured default obfuscator")
    void testEnabledByDefaultWithConfiguredDefaultObfuscator() {
        contextRunner
                .withUserConfiguration(ObfuscatorAutoConfiguration.class)
                .withPropertyValues("obfuscation.default-obfuscator.fixed-length=8")
                .run(context -> {
                    BeanWithObfuscators bean = context.getBean(BeanWithObfuscators.class);

                    assertThat(bean.defaultObfuscator).isEqualTo(Obfuscator.fixedLength(8));
                    assertThat(bean.annotatedObfuscator).isEqualTo(Obfuscator.fixedValue("<fixed>"));
                });
    }

    @Test
    @DisplayName("disabled")
    void testDisabled() {
        contextRunner
                .withPropertyValues("obfuscation.obfuscator-support.enabled=false")
                // with ObfuscatedSupportAutoConfiguration disabled, injecting Obfuscator will not be allowed
                .run(context -> assertThat(context).hasFailed());
    }

    @Component
    static class BeanWithObfuscators {

        @Autowired
        private Obfuscator defaultObfuscator;

        @Autowired
        @ObfuscateFixedValue("<fixed>")
        private Obfuscator annotatedObfuscator;
    }
}
