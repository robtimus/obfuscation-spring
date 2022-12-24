/*
 * ObfuscatorPropertiesTest.java
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscatorProvider;
import com.github.robtimus.obfuscation.spring.boot.autoconfigure.ObfuscatorProperties.ObfuscationMode;

@SuppressWarnings("nls")
final class ObfuscatorPropertiesTest {

    private static Validator validator;

    private ObfuscatorPropertiesTest() {
     // This constructor is only there to keep Checkstyle happy
     // JUnit accepts it just perfectly
    }

    @BeforeAll
    @SuppressWarnings("resource")
    static void setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    @DisplayName("validation")
    class ValidationTest {

        @Test
        @DisplayName("no properties set")
        void testNoProperties() {
            ObfuscatorProperties properties = new ObfuscatorProperties();

            Set<ConstraintViolation<ObfuscatorProperties>> violations = validator.validate(properties);

            assertThat(violations, contains(matchesViolation(equalTo(Messages.ObfuscatorProperties.noObfuscationModes()), equalTo("mode"))));
        }

        @Nested
        @DisplayName("only mode set")
        class TestNoPropertiesButMode {

            @Test
            @DisplayName("ALL")
            void testModeAll() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList();
                testAllProperties(ObfuscationMode.ALL, asList);
            }

            @Test
            @DisplayName("NONE")
            void testModeNone() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList();
                testAllProperties(ObfuscationMode.NONE, asList);
            }

            @Test
            @DisplayName("FIXED_LENGTH")
            void testModeFixedLength() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(notNullMessage(), equalTo("fixedLength")));
                testAllProperties(ObfuscationMode.FIXED_LENGTH, asList);
            }

            @Test
            @DisplayName("FIXED_VALUE")
            void testModeFixedValue() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(notNullMessage(), equalTo("fixedValue")));
                testAllProperties(ObfuscationMode.FIXED_VALUE, asList);
            }

            @Test
            @DisplayName("PORTION")
            void testModePortion() {
                // No required properties
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList();
                testAllProperties(ObfuscationMode.PORTION, asList);
            }

            @Test
            @DisplayName("PROVIDER")
            void testModeClass() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(notNullMessage(), equalTo("providerClass")));
                testAllProperties(ObfuscationMode.PROVIDER, asList);
            }

            private void testAllProperties(ObfuscationMode mode,
                    Collection<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> violationMatchers) {

                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(mode);

                Set<ConstraintViolation<ObfuscatorProperties>> violations = validator.validate(properties);

                assertThat(violations, containsInAnyOrder(violationMatchers));
            }
        }

        @Test
        @DisplayName("all properties but mode")
        void testAllPropertiesButMode() {
            ObfuscatorProperties properties = new ObfuscatorProperties();
            properties.setMaskChar('*');
            properties.setFixedLength(8);
            properties.setFixedValue("<fixed>");
            properties.setKeepAtStart(0);
            properties.setKeepAtEnd(0);
            properties.setAtLeastFromStart(0);
            properties.setAtLeastFromEnd(0);
            properties.setFixedTotalLength(8);
            properties.setProviderClass(TestObfuscatorProvider.class);

            Set<ConstraintViolation<ObfuscatorProperties>> violations = validator.validate(properties);

            assertThat(violations, contains(
                    matchesViolation(equalTo(Messages.ObfuscatorProperties.multipleObfuscationModes()), equalTo("mode"))));
        }

        @Nested
        @DisplayName("all properties")
        class TestAllProperties {

            @Test
            @DisplayName("ALL")
            void testModeAll() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(nullMessage(), equalTo("fixedLength")),
                        matchesViolation(nullMessage(), equalTo("fixedValue")),
                        matchesViolation(nullMessage(), equalTo("keepAtStart")),
                        matchesViolation(nullMessage(), equalTo("keepAtEnd")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromStart")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromEnd")),
                        matchesViolation(nullMessage(), equalTo("fixedTotalLength")),
                        matchesViolation(nullMessage(), equalTo("providerClass")));
                testAllProperties(ObfuscationMode.ALL, asList);
            }

            @Test
            @DisplayName("NONE")
            void testModeNone() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(nullMessage(), equalTo("maskChar")),
                        matchesViolation(nullMessage(), equalTo("fixedLength")),
                        matchesViolation(nullMessage(), equalTo("fixedValue")),
                        matchesViolation(nullMessage(), equalTo("keepAtStart")),
                        matchesViolation(nullMessage(), equalTo("keepAtEnd")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromStart")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromEnd")),
                        matchesViolation(nullMessage(), equalTo("fixedTotalLength")),
                        matchesViolation(nullMessage(), equalTo("providerClass")));
                testAllProperties(ObfuscationMode.NONE, asList);
            }

            @Test
            @DisplayName("FIXED_LENGTH")
            void testModeFixedLength() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(nullMessage(), equalTo("fixedValue")),
                        matchesViolation(nullMessage(), equalTo("keepAtStart")),
                        matchesViolation(nullMessage(), equalTo("keepAtEnd")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromStart")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromEnd")),
                        matchesViolation(nullMessage(), equalTo("fixedTotalLength")),
                        matchesViolation(nullMessage(), equalTo("providerClass")));
                testAllProperties(ObfuscationMode.FIXED_LENGTH, asList);
            }

            @Test
            @DisplayName("FIXED_VALUE")
            void testModeFixedValue() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(nullMessage(), equalTo("maskChar")),
                        matchesViolation(nullMessage(), equalTo("fixedLength")),
                        matchesViolation(nullMessage(), equalTo("keepAtStart")),
                        matchesViolation(nullMessage(), equalTo("keepAtEnd")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromStart")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromEnd")),
                        matchesViolation(nullMessage(), equalTo("fixedTotalLength")),
                        matchesViolation(nullMessage(), equalTo("providerClass")));
                testAllProperties(ObfuscationMode.FIXED_VALUE, asList);
            }

            @Test
            @DisplayName("PORTION")
            void testModePortion() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(nullMessage(), equalTo("fixedLength")),
                        matchesViolation(nullMessage(), equalTo("fixedValue")),
                        matchesViolation(nullMessage(), equalTo("providerClass")));
                testAllProperties(ObfuscationMode.PORTION, asList);
            }

            @Test
            @DisplayName("PROVIDER")
            void testModeClass() {
                List<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> asList = Arrays.asList(
                        matchesViolation(nullMessage(), equalTo("maskChar")),
                        matchesViolation(nullMessage(), equalTo("fixedLength")),
                        matchesViolation(nullMessage(), equalTo("fixedValue")),
                        matchesViolation(nullMessage(), equalTo("keepAtStart")),
                        matchesViolation(nullMessage(), equalTo("keepAtEnd")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromStart")),
                        matchesViolation(nullMessage(), equalTo("atLeastFromEnd")),
                        matchesViolation(nullMessage(), equalTo("fixedTotalLength")));
                testAllProperties(ObfuscationMode.PROVIDER, asList);
            }

            private void testAllProperties(ObfuscationMode mode,
                    Collection<Matcher<? super ConstraintViolation<ObfuscatorProperties>>> violationMatchers) {

                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(mode);
                properties.setMaskChar('*');
                properties.setFixedLength(8);
                properties.setFixedValue("<fixed>");
                properties.setKeepAtStart(0);
                properties.setKeepAtEnd(0);
                properties.setAtLeastFromStart(0);
                properties.setAtLeastFromEnd(0);
                properties.setFixedTotalLength(8);
                properties.setProviderClass(TestObfuscatorProvider.class);

                Set<ConstraintViolation<ObfuscatorProperties>> violations = validator.validate(properties);

                assertThat(violations, containsInAnyOrder(violationMatchers));
            }
        }

        @Nested
        @DisplayName("minimal properties")
        class TestMinimalProperties {

            @Test
            @DisplayName("ALL")
            void testModeAll() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                // mode must be set
                properties.setMode(ObfuscationMode.ALL);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.ALL, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("NONE")
            void testModeNone() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                // mode must be set
                properties.setMode(ObfuscationMode.NONE);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.NONE, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("FIXED_LENGTH")
            void testModeFixedLength() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setFixedLength(8);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.FIXED_LENGTH, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("FIXED_VALUE")
            void testModeFixedValue() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setFixedValue("<fixed>");

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.FIXED_VALUE, properties.determineObfuscationMode());
            }

            @Nested
            @DisplayName("PORTION")
            class PortionTest {

                @DisplayName("mode")
                void testModeProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setMode(ObfuscationMode.PORTION);

                    assertEquals(Collections.emptySet(), validator.validate(properties));

                    assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
                }

                @DisplayName("keepAtStart")
                void testKeepAtStartProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setKeepAtStart(0);

                    assertEquals(Collections.emptySet(), validator.validate(properties));

                    assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
                }

                @DisplayName("keepAtEnd")
                void testKeepAtEndProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setKeepAtEnd(0);

                    assertEquals(Collections.emptySet(), validator.validate(properties));

                    assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
                }

                @DisplayName("atLeastFromStart")
                void testAtLeastFromStartProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setAtLeastFromStart(0);

                    assertEquals(Collections.emptySet(), validator.validate(properties));

                    assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
                }

                @DisplayName("atLeastFromEnd")
                void testAtLeastFromEndProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setAtLeastFromEnd(0);

                    assertEquals(Collections.emptySet(), validator.validate(properties));

                    assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
                }

                @DisplayName("fixedTotalLength")
                void testFixedTotalLengthProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setFixedTotalLength(8);

                    assertEquals(Collections.emptySet(), validator.validate(properties));

                    assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
                }
            }

            @Test
            @DisplayName("PROVIDER")
            void testModeClass() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setProviderClass(TestObfuscatorProvider.class);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.PROVIDER, properties.determineObfuscationMode());
            }
        }

        @Nested
        @DisplayName("full properties")
        class TestFullProperties {

            @Test
            @DisplayName("ALL")
            void testModeAll() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.ALL);
                properties.setMaskChar('*');

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.ALL, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("NONE")
            void testModeNone() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.NONE);
                // No other properties are allowed

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.NONE, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("FIXED_LENGTH")
            void testModeFixedLength() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.FIXED_LENGTH);
                properties.setMaskChar('*');
                properties.setFixedLength(8);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.FIXED_LENGTH, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("FIXED_VALUE")
            void testModeFixedValue() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.FIXED_VALUE);
                properties.setFixedValue("<fixed>");
                // No other properties are allowed

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.FIXED_VALUE, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("PORTION")
            void testModePortion() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.PORTION);
                properties.setKeepAtStart(0);
                properties.setKeepAtEnd(0);
                properties.setAtLeastFromStart(0);
                properties.setAtLeastFromEnd(0);
                properties.setFixedTotalLength(8);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.PORTION, properties.determineObfuscationMode());
            }

            @Test
            @DisplayName("PROVIDER")
            void testModeClass() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.PROVIDER);
                properties.setProviderClass(TestObfuscatorProvider.class);

                assertEquals(Collections.emptySet(), validator.validate(properties));

                assertEquals(ObfuscationMode.PROVIDER, properties.determineObfuscationMode());
            }
        }

        private Matcher<String> notNullMessage() {
            return equalTo("must not be null");
        }

        private Matcher<String> nullMessage() {
            return equalTo("must be null");
        }

        Matcher<ConstraintViolation<ObfuscatorProperties>> matchesViolation(Matcher<String> messageMatcher, Matcher<String> pathMatcher) {
            return allOf(
                    hasProperty("message", messageMatcher),
                    hasProperty("propertyPath", hasToString(pathMatcher)));
        }
    }

    @Nested
    @DisplayName("createObfuscator")
    class CreateObfuscatorTest {

        private AutowireCapableBeanFactory beanFactory;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void setupBeanFactory() {
            beanFactory = mock(AutowireCapableBeanFactory.class);
            doAnswer(i -> instance(i.getArgument(0, Class.class))).when(beanFactory).getBeanProvider(any(Class.class));
        }

        private ObjectProvider<?> instance(Class<?> type) {
            ObjectProvider<?> objectProvider = mock(ObjectProvider.class);
            Object instance = assertDoesNotThrow(() -> {
                Constructor<?> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            });
            doReturn(instance).when(objectProvider).getIfAvailable();
            doReturn(instance).when(objectProvider).getIfAvailable(any());
            return objectProvider;
        }

        @Test
        @DisplayName("no properties set")
        void testNoProperties() {
            ObfuscatorProperties properties = new ObfuscatorProperties();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.createObfuscator(beanFactory));
            assertEquals(Messages.ObfuscatorProperties.noObfuscationModes(), exception.getMessage());
        }

        @Test
        @DisplayName("all properties but mode")
        void testAllPropertiesButMode() {
            ObfuscatorProperties properties = new ObfuscatorProperties();
            properties.setMaskChar('*');
            properties.setFixedLength(8);
            properties.setFixedValue("<fixed>");
            properties.setKeepAtStart(0);
            properties.setKeepAtEnd(0);
            properties.setAtLeastFromStart(0);
            properties.setAtLeastFromEnd(0);
            properties.setFixedTotalLength(8);
            properties.setProviderClass(TestObfuscatorProvider.class);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.createObfuscator(beanFactory));
            assertEquals(Messages.ObfuscatorProperties.multipleObfuscationModes(), exception.getMessage());
        }

        @Nested
        @DisplayName("ALL")
        class AllTest {

            @Test
            @DisplayName("minimal")
            void testMinimal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.ALL);

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.all(), obfuscator);
            }

            @Test
            @DisplayName("maximal")
            void testMaximal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.ALL);
                properties.setMaskChar('x');

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.all('x'), obfuscator);
            }
        }

        @Test
        @DisplayName("NONE")
        void testNone() {
            ObfuscatorProperties properties = new ObfuscatorProperties();
            properties.setMode(ObfuscationMode.NONE);

            Obfuscator obfuscator = properties.createObfuscator(beanFactory);
            assertEquals(Obfuscator.none(), obfuscator);
        }

        @Nested
        @DisplayName("FIXED_LENGTH")
        class FixedLengthTest {

            @Test
            @DisplayName("missing fixed length")
            void testMissingFixedLength() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.FIXED_LENGTH);

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.createObfuscator(beanFactory));
                assertEquals(Messages.ObfuscatorProperties.missingProperty("fixedLength"), exception.getMessage());
            }

            @Test
            @DisplayName("minimal")
            void testMinimal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setFixedLength(8);

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.fixedLength(8), obfuscator);
            }

            @Test
            @DisplayName("maximal")
            void testMaximal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.FIXED_LENGTH);
                properties.setFixedLength(8);
                properties.setMaskChar('x');

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.fixedLength(8, 'x'), obfuscator);
            }
        }

        @Nested
        @DisplayName("FIXED_VALUE")
        class FixedValueTest {

            @Test
            @DisplayName("missing fixed value")
            void testMissingFixedValue() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.FIXED_VALUE);

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.createObfuscator(beanFactory));
                assertEquals(Messages.ObfuscatorProperties.missingProperty("fixedValue"), exception.getMessage());
            }

            @Test
            @DisplayName("minimal")
            void testMinimal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setFixedValue("<fixed>");

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.fixedValue("<fixed>"), obfuscator);
            }

            @Test
            @DisplayName("maximal")
            void testMaximal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.FIXED_VALUE);
                properties.setFixedValue("<fixed>");

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.fixedValue("<fixed>"), obfuscator);
            }
        }

        @Nested
        @DisplayName("PORTION")
        class PortionTest {

            @Nested
            @DisplayName("minimal")
            class Minimal {

                @Test
                @DisplayName("mode")
                void testModeProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setMode(ObfuscationMode.PORTION);

                    Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                    assertEquals(Obfuscator.portion().build(), obfuscator);
                }

                @Test
                @DisplayName("keepAtStart")
                void testKeepAtStartProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setKeepAtStart(1);

                    Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                    assertEquals(Obfuscator.portion().keepAtStart(1).build(), obfuscator);
                }

                @Test
                @DisplayName("keepAtEnd")
                void testKeepAtEndProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setKeepAtEnd(1);

                    Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                    assertEquals(Obfuscator.portion().keepAtEnd(1).build(), obfuscator);
                }

                @Test
                @DisplayName("atLeastFromStart")
                void testAtLeastFromStartProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setAtLeastFromStart(1);

                    Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                    assertEquals(Obfuscator.portion().atLeastFromStart(1).build(), obfuscator);
                }

                @Test
                @DisplayName("atLeastFromEnd")
                void testAtLeastFromEndProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setAtLeastFromEnd(1);

                    Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                    assertEquals(Obfuscator.portion().atLeastFromEnd(1).build(), obfuscator);
                }

                @Test
                @DisplayName("fixedTotalLength")
                void testFixedTotalLengthProperty() {
                    ObfuscatorProperties properties = new ObfuscatorProperties();
                    properties.setFixedTotalLength(8);

                    Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                    assertEquals(Obfuscator.portion().withFixedTotalLength(8).build(), obfuscator);
                }
            }

            @Test
            @DisplayName("maximal")
            void testMaximal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.PORTION);
                properties.setKeepAtStart(1);
                properties.setKeepAtEnd(2);
                properties.setAtLeastFromStart(3);
                properties.setAtLeastFromEnd(4);
                properties.setFixedTotalLength(5);
                properties.setMaskChar('x');

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertEquals(Obfuscator.portion()
                        .keepAtStart(1)
                        .keepAtEnd(2)
                        .atLeastFromStart(3)
                        .atLeastFromEnd(4)
                        .withFixedTotalLength(5)
                        .withMaskChar('x')
                        .build(), obfuscator);
            }
        }

        @Nested
        @DisplayName("PROVIDER")
        class ClassTest {

            @Test
            @DisplayName("missing class name")
            void testMissingClassName() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.PROVIDER);

                IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.createObfuscator(beanFactory));
                assertEquals(Messages.ObfuscatorProperties.missingProperty("providerClass"), exception.getMessage());
            }

            @Test
            @DisplayName("invalid class")
            void testInvalidClass() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                @SuppressWarnings("unchecked")
                Class<? extends ObfuscatorProvider> providerClass = (Class<? extends ObfuscatorProvider>) (Class<?>) String.class;
                assertThrows(ClassCastException.class, () -> properties.setProviderClass(providerClass));
            }

            @Test
            @DisplayName("minimal")
            void testMinimal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setProviderClass(TestObfuscatorProvider.class);

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertSame(TestObfuscatorProvider.OBFUSCATOR, obfuscator);
            }

            @Test
            @DisplayName("maximal")
            void testMaximal() {
                ObfuscatorProperties properties = new ObfuscatorProperties();
                properties.setMode(ObfuscationMode.PROVIDER);
                properties.setProviderClass(TestObfuscatorProvider.class);

                Obfuscator obfuscator = properties.createObfuscator(beanFactory);
                assertSame(TestObfuscatorProvider.OBFUSCATOR, obfuscator);
            }
        }
    }

    @Nested
    @DisplayName("Spring Boot auto-setting of providerClass")
    class SpringBootSettingProviderClass {

        @Test
        @DisplayName("valid class")
        void testValidClass() {
            ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(PropertiesProvider.class));

            contextRunner.withPropertyValues("obfuscator.provider-class=" + TestObfuscatorProvider.class.getName())
                    .run(context -> {
                        ObfuscatorProperties properties = context.getBean(ObfuscatorProperties.class);
                        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();

                        Obfuscator obfuscator = properties.createObfuscator(beanFactory);

                        assertSame(TestObfuscatorProvider.OBFUSCATOR, obfuscator);
                    });
        }

        @Test
        @DisplayName("invalid class")
        void testInvalidClass() {
            ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(PropertiesProvider.class));

            contextRunner.withPropertyValues("obfuscator.provider-class=" + String.class.getName())
                    .run(context -> Assertions.assertThat(context).hasFailed());
        }

        @Test
        @DisplayName("non-existing class")
        void testNonExistingClass() {
            ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(PropertiesProvider.class));

            contextRunner.withPropertyValues("obfuscator.provider-class=" + TestObfuscatorProvider.class.getName() + "NonExisting")
                    .run(context -> Assertions.assertThat(context).hasFailed());
        }
    }

    @Configuration
    @EnableAutoConfiguration
    static class PropertiesProvider {

        @Bean
        @ConfigurationProperties(prefix = "obfuscator", ignoreUnknownFields = false, ignoreInvalidFields = false)
        ObfuscatorProperties obfuscatorProperties() {
            return new ObfuscatorProperties();
        }
    }
}
