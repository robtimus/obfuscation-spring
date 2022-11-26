/*
 * FullAutoConfigurationTest.java
 * Copyright 2022 Rob Spoor
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.spring.ObfuscatedSupportBeanFactoryPostProcessor;
import com.github.robtimus.obfuscation.spring.ObfuscatorSupportBeanFactoryPostProcessor;

@SuppressWarnings("nls")
class FullAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AutoConfigurationLoader.class));

    @Test
    void testWithNoProperties() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ObfuscatedSupportBeanFactoryPostProcessor.class);
            assertThat(context).doesNotHaveBean(ObfuscatorProperties.class);
            assertThat(context).doesNotHaveBean(Obfuscator.class);
            assertThat(context).hasSingleBean(ObfuscatorSupportBeanFactoryPostProcessor.class);
        });
    }

    @Test
    void testWithDefaultProperties() {
        contextRunner
                .withPropertyValues("obfuscation.default-obfuscator.fixed-length=3")
                .run(context -> {
                    assertThat(context).hasSingleBean(ObfuscatedSupportBeanFactoryPostProcessor.class);
                    assertThat(context).hasSingleBean(ObfuscatorProperties.class);
                    assertThat(context).hasSingleBean(Obfuscator.class);
                    assertThat(context).hasSingleBean(ObfuscatorSupportBeanFactoryPostProcessor.class);

                    Obfuscator defaultObfuscator = context.getBean(Obfuscator.class);
                    assertEquals("***", defaultObfuscator.obfuscateText("Hello world").toString());
                });
    }

    @Configuration
    @EnableAutoConfiguration
    static class AutoConfigurationLoader {
        // no content
    }
}
