/*
 * ObfuscatorSupportBeanFactoryPostProcessorTest.java
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

import static com.github.robtimus.obfuscation.Obfuscator.fixedValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.github.robtimus.obfuscation.Obfuscated;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscateUsing;

@SuppressWarnings("nls")
class ObfuscatorSupportBeanFactoryPostProcessorTest {

    @Nested
    @DisplayName("autowired obfuscator field")
    class AutowiredObfuscatorField {

        @Nested
        @DisplayName("with default obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, BeanWithAutowiredObfuscatorField.class })
        class WithDefaultObfuscator {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                BeanWithAutowiredObfuscatorField bean = context.getBean(BeanWithAutowiredObfuscatorField.class);

                Obfuscator expected = ObfuscatorSupport.DEFAULT_OBFUSCATOR;

                assertEquals(expected, bean.obfuscator);

                assertNotEquals(expected, bean.lazyObfuscator);

                String text = UUID.randomUUID().toString();
                assertEquals(expected.obfuscateText(text).toString(), bean.lazyObfuscator.obfuscateText(text).toString());
            }
        }

        @Nested
        @DisplayName("with obfuscator bean")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, ObfuscatorProvider.class, BeanWithAutowiredObfuscatorField.class })
        class WithObfuscatorBean {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                BeanWithAutowiredObfuscatorField bean = context.getBean(BeanWithAutowiredObfuscatorField.class);

                Obfuscator expected = ObfuscatorProvider.DEFAULT_OBFUSCATOR;

                assertEquals(expected, bean.obfuscator);

                assertNotEquals(expected, bean.lazyObfuscator);

                String text = UUID.randomUUID().toString();
                assertEquals(expected.obfuscateText(text).toString(), bean.lazyObfuscator.obfuscateText(text).toString());
            }
        }

        @Nested
        @DisplayName("with annotated obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, BeanWithAutowiredAnnotatedObfuscatorField.class })
        class WithAnnotatedObfuscator {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                BeanWithAutowiredAnnotatedObfuscatorField bean = context.getBean(BeanWithAutowiredAnnotatedObfuscatorField.class);

                Obfuscator expected = TestObfuscatorProvider.OBFUSCATOR;

                assertEquals(expected, bean.obfuscator);

                assertNotEquals(expected, bean.lazyObfuscator);

                String text = UUID.randomUUID().toString();
                assertEquals(expected.obfuscateText(text).toString(), bean.lazyObfuscator.obfuscateText(text).toString());
            }
        }
    }

    @Nested
    @DisplayName("autowired obfuscator constructor argument")
    class AutowiredObfuscatorConstructorArgument {

        @Nested
        @DisplayName("with default obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, BeanWithObfuscatorConstructorArgument.class })
        class WithDefaultObfuscator {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                BeanWithObfuscatorConstructorArgument bean = context.getBean(BeanWithObfuscatorConstructorArgument.class);

                Obfuscator expected = ObfuscatorSupport.DEFAULT_OBFUSCATOR;

                assertEquals(expected, bean.obfuscator);

                assertNotEquals(expected, bean.lazyObfuscator);

                String text = UUID.randomUUID().toString();
                assertEquals(expected.obfuscateText(text).toString(), bean.lazyObfuscator.obfuscateText(text).toString());
            }
        }

        @Nested
        @DisplayName("with obfuscator bean")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, ObfuscatorProvider.class, BeanWithObfuscatorConstructorArgument.class })
        class WithObfuscatorBean {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                BeanWithObfuscatorConstructorArgument bean = context.getBean(BeanWithObfuscatorConstructorArgument.class);

                Obfuscator expected = ObfuscatorProvider.DEFAULT_OBFUSCATOR;

                assertEquals(expected, bean.obfuscator);

                assertNotEquals(expected, bean.lazyObfuscator);

                String text = UUID.randomUUID().toString();
                assertEquals(expected.obfuscateText(text).toString(), bean.lazyObfuscator.obfuscateText(text).toString());
            }
        }

        @Nested
        @DisplayName("with annotated obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, BeanWithAnnotatedObfuscatorConstructorArgument.class })
        class WithAnnotatedObfuscator {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                BeanWithAnnotatedObfuscatorConstructorArgument bean = context.getBean(BeanWithAnnotatedObfuscatorConstructorArgument.class);

                Obfuscator expected = TestObfuscatorProvider.OBFUSCATOR;

                assertEquals(expected, bean.obfuscator);

                assertNotEquals(expected, bean.lazyObfuscator);

                String text = UUID.randomUUID().toString();
                assertEquals(expected.obfuscateText(text).toString(), bean.lazyObfuscator.obfuscateText(text).toString());
            }
        }
    }

    @Nested
    @DisplayName("autowired obfuscator method argument")
    class AutowiredObfuscatorMethodArgument {

        @Nested
        @DisplayName("with default obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, BeanWithObfuscatorMethodArgument.class })
        class WithDefaultObfuscator {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorSupport.DEFAULT_OBFUSCATOR;

                @SuppressWarnings("unchecked")
                Obfuscated<String> obfuscated = context.getBean(Obfuscated.class);

                Obfuscated<String> expected = obfuscator.obfuscateObject(obfuscated.value());
                assertEquals(expected, obfuscated);
                assertEquals(expected.toString(), obfuscated.toString());
            }
        }

        @Nested
        @DisplayName("with obfuscator bean")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, ObfuscatorProvider.class, BeanWithObfuscatorMethodArgument.class })
        class WithObfuscatorBean {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorProvider.DEFAULT_OBFUSCATOR;

                @SuppressWarnings("unchecked")
                Obfuscated<String> obfuscated = context.getBean(Obfuscated.class);

                Obfuscated<String> expected = obfuscator.obfuscateObject(obfuscated.value());
                assertEquals(expected, obfuscated);
                assertEquals(expected.toString(), obfuscated.toString());
            }
        }

        @Nested
        @DisplayName("with annotated obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, BeanWithAnnotatedObfuscatorMethodArgument.class })
        class WithAnnotatedObfuscator {

            @Autowired
            private ApplicationContext context;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = TestObfuscatorProvider.OBFUSCATOR;

                @SuppressWarnings("unchecked")
                Obfuscated<String> obfuscated = context.getBean(Obfuscated.class);

                Obfuscated<String> expected = obfuscator.obfuscateObject(obfuscated.value());
                assertEquals(expected, obfuscated);
                assertEquals(expected.toString(), obfuscated.toString());
            }
        }
    }

    @Configuration
    static class ProcessorProvider {

        @Bean("oacrpp")
        static ObfuscatorSupportBeanFactoryPostProcessor postProcessor() {
            return new ObfuscatorSupportBeanFactoryPostProcessor();
        }
    }

    @Configuration
    static class ObfuscatorProvider {

        static final Obfuscator DEFAULT_OBFUSCATOR = fixedValue("<default>");

        @Bean
        Obfuscator defaultObfuscator() {
            return DEFAULT_OBFUSCATOR;
        }

        @Bean
        LocalDate today() {
            return LocalDate.now();
        }
    }

    static class BeanWithAutowiredObfuscatorField {

        @Autowired
        private Obfuscator obfuscator;

        @Autowired
        @Lazy
        private Obfuscator lazyObfuscator;
    }

    static class BeanWithAutowiredAnnotatedObfuscatorField {

        @Autowired
        @ObfuscateUsing(TestObfuscatorProvider.class)
        private Obfuscator obfuscator;

        @Autowired
        @Lazy
        @ObfuscateUsing(TestObfuscatorProvider.class)
        private Obfuscator lazyObfuscator;
    }

    static class BeanWithObfuscatorConstructorArgument {

        private final Obfuscator obfuscator;
        private final Obfuscator lazyObfuscator;

        BeanWithObfuscatorConstructorArgument(Obfuscator obfuscator, @Lazy Obfuscator lazyObfuscator) {
            this.obfuscator = Objects.requireNonNull(obfuscator);
            this.lazyObfuscator = Objects.requireNonNull(lazyObfuscator);
        }
    }

    static class BeanWithAnnotatedObfuscatorConstructorArgument {

        private final Obfuscator obfuscator;
        private final Obfuscator lazyObfuscator;

        BeanWithAnnotatedObfuscatorConstructorArgument(@ObfuscateUsing(TestObfuscatorProvider.class) Obfuscator obfuscator,
                @Lazy @ObfuscateUsing(TestObfuscatorProvider.class) Obfuscator lazyObfuscator) {

            this.obfuscator = Objects.requireNonNull(obfuscator);
            this.lazyObfuscator = Objects.requireNonNull(lazyObfuscator);
        }
    }

    static class BeanWithObfuscatorMethodArgument {

        @Bean
        Obfuscated<String> obfuscatedValue(Obfuscator obfuscator) {
            return obfuscator.obfuscateObject(ObfuscatorSupportBeanFactoryPostProcessorTest.class.getName());
        }
    }

    static class BeanWithAnnotatedObfuscatorMethodArgument {

        @Bean
        Obfuscated<String> obfuscatedValue(@ObfuscateUsing(TestObfuscatorProvider.class) Obfuscator obfuscator) {
            return obfuscator.obfuscateObject(ObfuscatorSupportBeanFactoryPostProcessorTest.class.getName());
        }
    }
}
