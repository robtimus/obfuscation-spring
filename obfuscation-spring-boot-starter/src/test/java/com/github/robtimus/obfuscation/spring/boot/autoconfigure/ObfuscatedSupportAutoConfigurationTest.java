/*
 * ObfuscatedSupportAutoConfigurationTest.java
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.stereotype.Component;
import com.github.robtimus.obfuscation.Obfuscated;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscateFixedValue;

@SuppressWarnings("nls")
class ObfuscatedSupportAutoConfigurationTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ObfuscatedSupportAutoConfiguration.class, BeanWithObfuscateds.class))
            .withPropertyValues("secure-value=12345");

    @Test
    @DisplayName("enabled by default with out-of-the-box default obfuscator")
    void testEnabledByDefault() {
        contextRunner.run(context -> {
            BeanWithObfuscateds bean = context.getBean(BeanWithObfuscateds.class);

            Obfuscator defaultObfuscator = Obfuscator.fixedLength(3);
            Obfuscator annotatedObfuscator = Obfuscator.fixedValue("<fixed>");

            String secureValue = context.getEnvironment().getProperty("secure-value");

            Obfuscated<String> defaultObfuscated = defaultObfuscator.obfuscateObject(secureValue);
            Obfuscated<String> annotatedObfuscated = annotatedObfuscator.obfuscateObject(secureValue);

            assertThat(bean.defaultObfuscated).isEqualTo(defaultObfuscated);
            assertThat(bean.defaultObfuscated).hasToString(defaultObfuscated.toString());

            assertThat(bean.annotatedObfuscated).isEqualTo(annotatedObfuscated);
            assertThat(bean.annotatedObfuscated).hasToString(annotatedObfuscated.toString());
        });
    }

    @Test
    @DisplayName("enabled by default with configured default obfuscated")
    void testEnabledByDefaultWithConfiguredDefaultObfuscator() {
        contextRunner
                .withUserConfiguration(ObfuscatorAutoConfiguration.class)
                .withPropertyValues("obfuscation.default-obfuscator.fixed-length=8")
                .run(context -> {
                    BeanWithObfuscateds bean = context.getBean(BeanWithObfuscateds.class);

                    Obfuscator defaultObfuscator = Obfuscator.fixedLength(8);
                    Obfuscator annotatedObfuscator = Obfuscator.fixedValue("<fixed>");

                    String secureValue = context.getEnvironment().getProperty("secure-value");

                    Obfuscated<String> defaultObfuscated = defaultObfuscator.obfuscateObject(secureValue);
                    Obfuscated<String> annotatedObfuscated = annotatedObfuscator.obfuscateObject(secureValue);

                    assertThat(bean.defaultObfuscated).isEqualTo(defaultObfuscated);
                    assertThat(bean.defaultObfuscated).hasToString(defaultObfuscated.toString());

                    assertThat(bean.annotatedObfuscated).isEqualTo(annotatedObfuscated);
                    assertThat(bean.annotatedObfuscated).hasToString(annotatedObfuscated.toString());
                });
    }

    @Test
    @DisplayName("disabled")
    void testDisabled() {
        contextRunner
                .withPropertyValues("obfuscation.obfuscated-support.enabled=false")
                // with ObfuscatedSupportAutoConfiguration disabled, injecting Obfuscated will not be allowed
                .run(context -> assertThat(context).hasFailed());
    }

    @Component
    static class BeanWithObfuscateds {

        @Value("${secure-value}")
        private Obfuscated<String> defaultObfuscated;

        @Value("${secure-value}")
        @ObfuscateFixedValue("<fixed>")
        private Obfuscated<String> annotatedObfuscated;
    }
}
