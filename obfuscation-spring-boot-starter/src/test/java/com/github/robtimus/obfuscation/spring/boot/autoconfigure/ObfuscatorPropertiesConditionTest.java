/*
 * ObfuscatorPropertiesConditionTest.java
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import com.github.robtimus.obfuscation.Obfuscator;

@SuppressWarnings("nls")
class ObfuscatorPropertiesConditionTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PropertiesProvider.class));

    @Test
    @DisplayName("no properties")
    void testNoProperties() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(ObfuscatorProperties.class);
        });
    }

    @Nested
    @DisplayName("single property")
    class SinglePropertyTest {

        @Test
        @DisplayName("mode")
        void testMode() {
            contextRunner.withPropertyValues("obfuscator-condition.mode=ALL")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.all());
                    });
        }

        @Test
        @DisplayName("maskChar")
        void testMaskChar() {
            contextRunner.withPropertyValues("obfuscator-condition.maskChar=*")
                    // with only the mask char set, the condition validation fails
                    .run(context -> assertThat(context).hasFailed());
        }

        @Test
        @DisplayName("mask-char")
        void testMaskCharKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.mask-char=*")
                    // with only the mask char set, the condition validation fails
                    .run(context -> assertThat(context).hasFailed());
        }

        @Test
        @DisplayName("fixedLength")
        void testFixedLength() {
            contextRunner.withPropertyValues("obfuscator-condition.fixedLength=8")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.fixedLength(8));
                    });
        }

        @Test
        @DisplayName("fixed-length")
        void testFixedLengthKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.fixed-length=8")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.fixedLength(8));
                    });
        }

        @Test
        @DisplayName("fixedValue")
        void testFixedValue() {
            contextRunner.withPropertyValues("obfuscator-condition.fixedValue=<fixed>")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.fixedValue("<fixed>"));
                    });
        }

        @Test
        @DisplayName("fixed-value")
        void testFixedValueKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.fixed-value=<fixed>")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.fixedValue("<fixed>"));
                    });
        }

        @Test
        @DisplayName("keepAtStart")
        void testKeepAtStart() {
            contextRunner.withPropertyValues("obfuscator-condition.keepAtStart=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().keepAtStart(1).build());
                    });
        }

        @Test
        @DisplayName("keep-at-start")
        void testKeepAtStartKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.keep-at-start=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().keepAtStart(1).build());
                    });
        }

        @Test
        @DisplayName("keepAtEnd")
        void testKeepAtEnd() {
            contextRunner.withPropertyValues("obfuscator-condition.keepAtEnd=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().keepAtEnd(1).build());
                    });
        }

        @Test
        @DisplayName("keep-at-end")
        void testKeepAtEndKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.keep-at-end=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().keepAtEnd(1).build());
                    });
        }

        @Test
        @DisplayName("atLeastFromStart")
        void testAtLeastFromStart() {
            contextRunner.withPropertyValues("obfuscator-condition.atLeastFromStart=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().atLeastFromStart(1).build());
                    });
        }

        @Test
        @DisplayName("at-least-from-start")
        void testAtLeastFromStartKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.at-least-from-start=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().atLeastFromStart(1).build());
                    });
        }

        @Test
        @DisplayName("atLeastFromEnd")
        void testAtLeastFromEnd() {
            contextRunner.withPropertyValues("obfuscator-condition.atLeastFromEnd=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().atLeastFromEnd(1).build());
                    });
        }

        @Test
        @DisplayName("at-least-from-end")
        void testAtLeastFromEndKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.at-least-from-end=1")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().atLeastFromEnd(1).build());
                    });
        }

        @Test
        @DisplayName("fixedTotalLength")
        void testFixedTotalLength() {
            contextRunner.withPropertyValues("obfuscator-condition.fixedTotalLength=8")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().withFixedTotalLength(8).build());
                    });
        }

        @Test
        @DisplayName("fixed-total-length")
        void testFixedTotalLengthKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.fixed-total-length=8")
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isEqualTo(Obfuscator.portion().withFixedTotalLength(8).build());
                    });
        }

        @Test
        @DisplayName("providerClass")
        void testProviderClass() {
            contextRunner.withPropertyValues("obfuscator-condition.providerClass=" + TestObfuscatorProvider.class.getName())
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isSameAs(TestObfuscatorProvider.OBFUSCATOR);
                    });
        }

        @Test
        @DisplayName("provider-class")
        void testProviderClassKebabCased() {
            contextRunner.withPropertyValues("obfuscator-condition.provider-class=" + TestObfuscatorProvider.class.getName())
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);

                        Obfuscator obfuscator = properties.createObfuscator(context.getAutowireCapableBeanFactory());

                        assertThat(obfuscator).isSameAs(TestObfuscatorProvider.OBFUSCATOR);
                    });
        }
    }

    static final class TestCondition extends ObfuscatorPropertiesCondition {

        private TestCondition() {
            super("obfuscator-condition");
        }
    }

    @Configuration
    @EnableAutoConfiguration
    static class PropertiesProvider {

        @Bean
        @ConfigurationProperties(prefix = "obfuscator-condition", ignoreUnknownFields = false, ignoreInvalidFields = false)
        @Conditional(TestCondition.class)
        ObfuscatorProperties obfuscatorProperties() {
            return new ObfuscatorProperties();
        }
    }
}
