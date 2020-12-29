/*
 * ObfuscatedSupportBeanFactoryPostProcessorTest.java
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.github.robtimus.obfuscation.Obfuscated;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscateUsing;
import com.github.robtimus.obfuscation.annotation.RepresentedBy;

@SuppressWarnings("nls")
class ObfuscatedSupportBeanFactoryPostProcessorTest {

    private static final String VALUE = "injectableValue";
    private static final String OBFUSCATED_STRING = "obfuscatedString";
    private static final String OBFUSCATED_LOCAL_DATE = "obfuscatedLocalDate";
    private static final String OBFUSCATED_LOCAL_DATE_TIME = "obfuscatedLocalDateTime";
    private static final String OBFUSCATED_DATE = "obfuscatedDate";

    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @Nested
    @DisplayName("autowired obfuscated field")
    class AutowiredObfuscatedField {

        @Nested
        @DisplayName("with default obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, LocalDateProvider.class, BeanWithAutowiredObfuscatedFields.class })
        class WithDefaultObfuscator {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorSupport.DEFAULT_OBFUSCATOR;
                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));

                BeanWithAutowiredObfuscatedFields bean = context.getBean(BeanWithAutowiredObfuscatedFields.class);

                assertEquals(expectedString, bean.obfuscatedString);
                assertEquals(expectedString.toString(), bean.obfuscatedString.toString());

                assertEquals(expectedLocalDate, bean.obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), bean.obfuscatedLocalDate.toString());

                assertEquals(expectedLocalDateTime, bean.obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), bean.obfuscatedLocalDateTime.toString());

                assertEquals(expectedDate, bean.obfuscatedDate);
                assertEquals(expectedDate.toString(), bean.obfuscatedDate.toString());
            }
        }

        @Nested
        @DisplayName("with obfuscator bean")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = {
                ProcessorProvider.class, LocalDateProvider.class, ObfuscatorProvider.class, BeanWithAutowiredObfuscatedFields.class
        })
        class WithObfuscatorBean {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorProvider.DEFAULT_OBFUSCATOR;
                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));

                BeanWithAutowiredObfuscatedFields bean = context.getBean(BeanWithAutowiredObfuscatedFields.class);

                assertEquals(expectedString, bean.obfuscatedString);
                assertEquals(expectedString.toString(), bean.obfuscatedString.toString());

                assertEquals(expectedLocalDate, bean.obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), bean.obfuscatedLocalDate.toString());

                assertEquals(expectedLocalDateTime, bean.obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), bean.obfuscatedLocalDateTime.toString());

                assertEquals(expectedDate, bean.obfuscatedDate);
                assertEquals(expectedDate.toString(), bean.obfuscatedDate.toString());
            }
        }

        @Nested
        @DisplayName("with annotated obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, LocalDateProvider.class, BeanWithAutowiredAnnotatedObfuscatedField.class })
        class WithAnnotatedObfuscator {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = TestObfuscatorProvider.OBFUSCATOR;
                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));

                BeanWithAutowiredAnnotatedObfuscatedField bean = context.getBean(BeanWithAutowiredAnnotatedObfuscatedField.class);

                assertEquals(expectedString, bean.obfuscatedString);
                assertNotEquals(expectedString.toString(), bean.obfuscatedString.toString());
                assertEquals(obfuscator.obfuscateText(TestCharacterRepresentationProvider.VALUE).toString(), bean.obfuscatedString.toString());

                assertEquals(expectedLocalDate, bean.obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), bean.obfuscatedLocalDate.toString());

                assertEquals(expectedLocalDateTime, bean.obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), bean.obfuscatedLocalDateTime.toString());

                assertEquals(expectedDate, bean.obfuscatedDate);
                assertEquals(expectedDate.toString(), bean.obfuscatedDate.toString());
            }
        }
    }

    @Nested
    @DisplayName("autowired obfuscator constructor argument")
    class AutowiredObfuscatorConstructorArgument {

        @Nested
        @DisplayName("with default obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, LocalDateProvider.class, BeanWithObfuscatedConstructorArgument.class })
        class WithDefaultObfuscator {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorSupport.DEFAULT_OBFUSCATOR;
                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));

                BeanWithObfuscatedConstructorArgument bean = context.getBean(BeanWithObfuscatedConstructorArgument.class);

                assertEquals(expectedString, bean.obfuscatedString);
                assertEquals(expectedString.toString(), bean.obfuscatedString.toString());

                assertEquals(expectedLocalDate, bean.obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), bean.obfuscatedLocalDate.toString());

                assertEquals(expectedLocalDateTime, bean.obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), bean.obfuscatedLocalDateTime.toString());

                assertEquals(expectedDate, bean.obfuscatedDate);
                assertEquals(expectedDate.toString(), bean.obfuscatedDate.toString());
            }
        }

        @Nested
        @DisplayName("with obfuscator bean")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = {
                ProcessorProvider.class, LocalDateProvider.class, ObfuscatorProvider.class, BeanWithObfuscatedConstructorArgument.class
        })
        class WithObfuscatorBean {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorProvider.DEFAULT_OBFUSCATOR;
                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));

                BeanWithObfuscatedConstructorArgument bean = context.getBean(BeanWithObfuscatedConstructorArgument.class);

                assertEquals(expectedString, bean.obfuscatedString);
                assertEquals(expectedString.toString(), bean.obfuscatedString.toString());

                assertEquals(expectedLocalDate, bean.obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), bean.obfuscatedLocalDate.toString());

                assertEquals(expectedLocalDateTime, bean.obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), bean.obfuscatedLocalDateTime.toString());

                assertEquals(expectedDate, bean.obfuscatedDate);
                assertEquals(expectedDate.toString(), bean.obfuscatedDate.toString());
            }
        }

        @Nested
        @DisplayName("with annotated obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, LocalDateProvider.class, BeanWithAnnotatedObfuscatedConstructorArgument.class })
        class WithAnnotatedObfuscator {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = TestObfuscatorProvider.OBFUSCATOR;
                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));

                BeanWithAnnotatedObfuscatedConstructorArgument bean = context.getBean(BeanWithAnnotatedObfuscatedConstructorArgument.class);

                assertEquals(expectedString, bean.obfuscatedString);
                assertNotEquals(expectedString.toString(), bean.obfuscatedString.toString());
                assertEquals(obfuscator.obfuscateText(TestCharacterRepresentationProvider.VALUE).toString(), bean.obfuscatedString.toString());

                assertEquals(expectedLocalDate, bean.obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), bean.obfuscatedLocalDate.toString());

                assertEquals(expectedLocalDateTime, bean.obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), bean.obfuscatedLocalDateTime.toString());

                assertEquals(expectedDate, bean.obfuscatedDate);
                assertEquals(expectedDate.toString(), bean.obfuscatedDate.toString());
            }
        }
    }

    @Nested
    @DisplayName("autowired obfuscated method argument")
    class AutowiredObfuscatedMethodArgument {

        @Nested
        @DisplayName("with default obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, LocalDateProvider.class, BeanWithObfuscatedMethodArgument.class })
        class WithDefaultObfuscator {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorSupport.DEFAULT_OBFUSCATOR;

                @SuppressWarnings("unchecked")
                Obfuscated<String> obfuscatedString = context.getBean(OBFUSCATED_STRING, Obfuscated.class);

                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                assertEquals(expectedString, obfuscatedString);
                assertEquals(expectedString.toString(), obfuscatedString.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<LocalDate> obfuscatedLocalDate = context.getBean(OBFUSCATED_LOCAL_DATE, Obfuscated.class);

                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                assertEquals(expectedLocalDate, obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), obfuscatedLocalDate.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<LocalDateTime> obfuscatedLocalDateTime = context.getBean(OBFUSCATED_LOCAL_DATE_TIME, Obfuscated.class);

                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                assertEquals(expectedLocalDateTime, obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), obfuscatedLocalDateTime.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<Date> obfuscatedDate = context.getBean(OBFUSCATED_DATE, Obfuscated.class);

                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));
                assertEquals(expectedDate, obfuscatedDate);
                assertEquals(expectedDate.toString(), obfuscatedDate.toString());
            }
        }

        @Nested
        @DisplayName("with obfuscator bean")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = {
                ProcessorProvider.class, LocalDateProvider.class, ObfuscatorProvider.class, BeanWithObfuscatedMethodArgument.class
        })
        class WithObfuscatorBean {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = ObfuscatorProvider.DEFAULT_OBFUSCATOR;

                @SuppressWarnings("unchecked")
                Obfuscated<String> obfuscatedString = context.getBean(OBFUSCATED_STRING, Obfuscated.class);

                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                assertEquals(expectedString, obfuscatedString);
                assertEquals(expectedString.toString(), obfuscatedString.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<LocalDate> obfuscatedLocalDate = context.getBean(OBFUSCATED_LOCAL_DATE, Obfuscated.class);

                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                assertEquals(expectedLocalDate, obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), obfuscatedLocalDate.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<LocalDateTime> obfuscatedLocalDateTime = context.getBean(OBFUSCATED_LOCAL_DATE_TIME, Obfuscated.class);

                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                assertEquals(expectedLocalDateTime, obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), obfuscatedLocalDateTime.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<Date> obfuscatedDate = context.getBean(OBFUSCATED_DATE, Obfuscated.class);

                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));
                assertEquals(expectedDate, obfuscatedDate);
                assertEquals(expectedDate.toString(), obfuscatedDate.toString());
            }
        }

        @Nested
        @DisplayName("with annotated obfuscator")
        @ExtendWith(SpringExtension.class)
        @ContextConfiguration(classes = { ProcessorProvider.class, LocalDateProvider.class, BeanWithAnnotatedObfuscatorMethodArgument.class })
        class WithAnnotatedObfuscator {

            @Autowired
            private ApplicationContext context;

            @Value(VALUE)
            private String value;

            @Test
            @DisplayName("autowiring")
            void testAutowiring() {
                Obfuscator obfuscator = TestObfuscatorProvider.OBFUSCATOR;

                @SuppressWarnings("unchecked")
                Obfuscated<String> obfuscatedString = context.getBean(OBFUSCATED_STRING, Obfuscated.class);

                Obfuscated<String> expectedString = obfuscator.obfuscateObject(value);
                assertEquals(expectedString, obfuscatedString);
                assertNotEquals(expectedString.toString(), obfuscatedString.toString());
                assertEquals(obfuscator.obfuscateText(TestCharacterRepresentationProvider.VALUE).toString(), obfuscatedString.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<LocalDate> obfuscatedLocalDate = context.getBean(OBFUSCATED_LOCAL_DATE, Obfuscated.class);

                Obfuscated<LocalDate> expectedLocalDate = obfuscator.obfuscateObject(LocalDate.now());
                assertEquals(expectedLocalDate, obfuscatedLocalDate);
                assertEquals(expectedLocalDate.toString(), obfuscatedLocalDate.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<LocalDateTime> obfuscatedLocalDateTime = context.getBean(OBFUSCATED_LOCAL_DATE_TIME, Obfuscated.class);

                Obfuscated<LocalDateTime> expectedLocalDateTime = obfuscator.obfuscateObject(LocalDateTime.now(CLOCK));
                assertEquals(expectedLocalDateTime, obfuscatedLocalDateTime);
                assertEquals(expectedLocalDateTime.toString(), obfuscatedLocalDateTime.toString());

                @SuppressWarnings("unchecked")
                Obfuscated<Date> obfuscatedDate = context.getBean(OBFUSCATED_DATE, Obfuscated.class);

                Obfuscated<Date> expectedDate = obfuscator.obfuscateObject(new Date(0));
                assertEquals(expectedDate, obfuscatedDate);
                assertEquals(expectedDate.toString(), obfuscatedDate.toString());
            }
        }
    }

    @Configuration
    static class ProcessorProvider {

        @Bean("otcpp")
        static ObfuscatedSupportBeanFactoryPostProcessor postProcessor() {
            return new ObfuscatedSupportBeanFactoryPostProcessor();
        }

        @Bean("opp")
        static BeanFactoryPostProcessor otherPostProcessor() {
            return beanFactory -> {
                DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
                AutowireCandidateResolver resolver = listableBeanFactory.getAutowireCandidateResolver();

                assertThat(resolver.getClass().getName(), startsWith(ObfuscatedSupportBeanFactoryPostProcessor.class.getName()));

                listableBeanFactory.setAutowireCandidateResolver(new AutowireCandidateResolver() {

                    @Override
                    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
                        return resolver.isAutowireCandidate(bdHolder, descriptor);
                    }

                    @Override
                    public boolean isRequired(DependencyDescriptor descriptor) {
                        return resolver.isRequired(descriptor);
                    }

                    @Override
                    public boolean hasQualifier(DependencyDescriptor descriptor) {
                        return resolver.hasQualifier(descriptor);
                    }

                    @Override
                    public Object getSuggestedValue(DependencyDescriptor descriptor) {
                        if (descriptor.getDependencyType() == LocalDateTime.class) {
                            return LocalDateTime.now(CLOCK);
                        }
                        if (descriptor.getDependencyType() == Date.class) {
                            return new Date(0);
                        }
                        return resolver.getSuggestedValue(descriptor);
                    }

                    @Override
                    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
                        return resolver.getLazyResolutionProxyIfNecessary(descriptor, beanName);
                    }

                    @Override
                    public AutowireCandidateResolver cloneIfNecessary() {
                        throw new UnsupportedOperationException();
                    }
                });
            };
        }
    }

    @Configuration
    static class LocalDateProvider {

        @Bean
        LocalDate today() {
            return LocalDate.now();
        }
    }

    @Configuration
    static class ObfuscatorProvider {

        static final Obfuscator DEFAULT_OBFUSCATOR = fixedValue("<default>");

        @Bean
        Obfuscator defaultObfuscator() {
            return DEFAULT_OBFUSCATOR;
        }
    }

    static class BeanWithAutowiredObfuscatedFields {

        @Autowired
        @Value(VALUE)
        private Obfuscated<String> obfuscatedString;

        @Autowired
        private Obfuscated<LocalDate> obfuscatedLocalDate;

        @Autowired
        private Obfuscated<LocalDateTime> obfuscatedLocalDateTime;

        @Autowired
        @Lazy
        private Obfuscated<Date> obfuscatedDate;
    }

    static class BeanWithAutowiredAnnotatedObfuscatedField {

        @Autowired
        @ObfuscateUsing(TestObfuscatorProvider.class)
        @RepresentedBy(TestCharacterRepresentationProvider.class)
        @Value(VALUE)
        private Obfuscated<String> obfuscatedString;

        @Autowired
        @ObfuscateUsing(TestObfuscatorProvider.class)
        private Obfuscated<LocalDate> obfuscatedLocalDate;

        @Autowired
        @ObfuscateUsing(TestObfuscatorProvider.class)
        private Obfuscated<LocalDateTime> obfuscatedLocalDateTime;

        @Autowired
        @Lazy
        @ObfuscateUsing(TestObfuscatorProvider.class)
        private Obfuscated<Date> obfuscatedDate;
    }

    static class BeanWithObfuscatedConstructorArgument {

        private final Obfuscated<String> obfuscatedString;
        private final Obfuscated<LocalDate> obfuscatedLocalDate;
        private final Obfuscated<LocalDateTime> obfuscatedLocalDateTime;
        private final Obfuscated<Date> obfuscatedDate;

        BeanWithObfuscatedConstructorArgument(@Value(VALUE) Obfuscated<String> obfuscatedString, Obfuscated<LocalDate> obfuscatedLocalDate,
                Obfuscated<LocalDateTime> obfuscatedLocalDateTime, @Lazy Obfuscated<Date> obfuscatedDate) {

            this.obfuscatedString = Objects.requireNonNull(obfuscatedString);
            this.obfuscatedLocalDate = Objects.requireNonNull(obfuscatedLocalDate);
            this.obfuscatedLocalDateTime = Objects.requireNonNull(obfuscatedLocalDateTime);
            this.obfuscatedDate = Objects.requireNonNull(obfuscatedDate);
        }
    }

    static class BeanWithAnnotatedObfuscatedConstructorArgument {

        private final Obfuscated<String> obfuscatedString;
        private final Obfuscated<LocalDate> obfuscatedLocalDate;
        private final Obfuscated<LocalDateTime> obfuscatedLocalDateTime;
        private final Obfuscated<Date> obfuscatedDate;

        BeanWithAnnotatedObfuscatedConstructorArgument(
                @Value(VALUE)
                @ObfuscateUsing(TestObfuscatorProvider.class)
                @RepresentedBy(TestCharacterRepresentationProvider.class)
                Obfuscated<String> obfuscatedString,
                @ObfuscateUsing(TestObfuscatorProvider.class) Obfuscated<LocalDate> obfuscatedLocalDate,
                @ObfuscateUsing(TestObfuscatorProvider.class) Obfuscated<LocalDateTime> obfuscatedLocalDateTime,
                @Lazy @ObfuscateUsing(TestObfuscatorProvider.class) Obfuscated<Date> obfuscatedDate) {

            this.obfuscatedString = Objects.requireNonNull(obfuscatedString);
            this.obfuscatedLocalDate = Objects.requireNonNull(obfuscatedLocalDate);
            this.obfuscatedLocalDateTime = Objects.requireNonNull(obfuscatedLocalDateTime);
            this.obfuscatedDate = Objects.requireNonNull(obfuscatedDate);
        }
    }

    static class BeanWithObfuscatedMethodArgument {

        @Bean(OBFUSCATED_STRING)
        Obfuscated<String> obfuscatedString(@Value(VALUE) Obfuscated<String> obfuscatedString) {
            return obfuscatedString;
        }

        @Bean(OBFUSCATED_LOCAL_DATE)
        Obfuscated<LocalDate> obfuscatedLocalDate(Obfuscated<LocalDate> obfuscatedLocalDate) {
            return obfuscatedLocalDate;
        }

        @Bean(OBFUSCATED_LOCAL_DATE_TIME)
        Obfuscated<LocalDateTime> obfuscatedLocalDateTime(Obfuscated<LocalDateTime> obfuscatedLocalDateTime) {
            return obfuscatedLocalDateTime;
        }

        @Bean(OBFUSCATED_DATE)
        Obfuscated<Date> obfuscatedDate(Obfuscated<Date> obfuscatedDate) {
            return obfuscatedDate;
        }
    }

    static class BeanWithAnnotatedObfuscatorMethodArgument {

        @Bean(OBFUSCATED_STRING)
        Obfuscated<String> obfuscatedString(
                @Value(VALUE)
                @ObfuscateUsing(TestObfuscatorProvider.class)
                @RepresentedBy(TestCharacterRepresentationProvider.class)
                Obfuscated<String> obfuscatedString) {

            return obfuscatedString;
        }

        @Bean(OBFUSCATED_LOCAL_DATE)
        Obfuscated<LocalDate> obfuscatedLocalDate(@ObfuscateUsing(TestObfuscatorProvider.class) Obfuscated<LocalDate> obfuscatedLocalDate) {
            return obfuscatedLocalDate;
        }

        @Bean(OBFUSCATED_LOCAL_DATE_TIME)
        Obfuscated<LocalDateTime> obfuscatedLocalDateTime(
                @ObfuscateUsing(TestObfuscatorProvider.class) Obfuscated<LocalDateTime> obfuscatedLocalDateTime) {

            return obfuscatedLocalDateTime;
        }

        @Bean(OBFUSCATED_DATE)
        Obfuscated<Date> obfuscatedDate(@Lazy @ObfuscateUsing(TestObfuscatorProvider.class) Obfuscated<Date> obfuscatedDate) {
            return obfuscatedDate;
        }
    }
}
