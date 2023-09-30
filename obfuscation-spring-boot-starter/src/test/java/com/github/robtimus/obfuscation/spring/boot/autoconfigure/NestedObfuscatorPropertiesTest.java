/*
 * NestedObfuscatorPropertiesTest.java
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.validation.annotation.Validated;
import com.github.robtimus.obfuscation.Obfuscator;

@SuppressWarnings("nls")
class NestedObfuscatorPropertiesTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ComplexProperties.class));

    @Test
    @DisplayName("no properties")
    void testNoProperties() {
        contextRunner.run(context -> {
            ComplexProperties propertiesWithObfuscators = context.getBean(ComplexProperties.class);
            assertThat(propertiesWithObfuscators.getObfuscators()).isNullOrEmpty();
            assertThat(propertiesWithObfuscators.getNested()).isNull();
        });
    }

    @Test
    @DisplayName("several properties")
    void testSeveralProperties() {
        contextRunner.withPropertyValues(
                "complex.obfuscators.0.mode=ALL",
                "complex.obfuscators.1.fixed-length=8", "complex.obfuscators.1.mask-char=x",
                "complex.obfuscators.2.fixed-value=<fixed>",
                "complex.nested.obfuscator.provider-class=" + TestObfuscatorProvider.class.getName(),
                "complex.nested.mapped.foo.mode=NONE",
                "complex.nested.mapped.bar.fixed-length=3"
                )
                .run(context -> {
                    List<Obfuscator> expected = Arrays.asList(
                            Obfuscator.all(),
                            Obfuscator.fixedLength(8, 'x'),
                            Obfuscator.fixedValue("<fixed>")
                            );

                    ComplexProperties complexProperties = context.getBean(ComplexProperties.class);
                    assertThat(complexProperties.getObfuscators()).isNotEmpty();

                    AutowireCapableBeanFactory beanFactory = context.getBeanFactory();
                    List<Obfuscator> obfuscators = ObfuscatorProperties.createObfuscators(complexProperties.getObfuscators(), beanFactory);
                    assertThat(obfuscators).isEqualTo(expected);

                    NestedProperties nested = complexProperties.getNested();
                    assertThat(nested).isNotNull();
                    assertThat(nested.getObfuscator()).isNotNull();

                    Obfuscator obfuscator = nested.getObfuscator().createObfuscator(beanFactory);
                    assertThat(obfuscator).isEqualTo(TestObfuscatorProvider.beanFactoryCreatedObfuscator());

                    Map<String, Obfuscator> mappedObfuscators = ObfuscatorProperties.createObfuscators(nested.getMapped(), beanFactory);
                    assertThat(mappedObfuscators).isNotNull();
                    assertThat(mappedObfuscators).containsEntry("foo", Obfuscator.none());
                    assertThat(mappedObfuscators).containsEntry("bar", Obfuscator.fixedLength(3));
                });
    }

    @Test
    @DisplayName("invalid properties")
    void testInvalidProperties() {
        contextRunner.withPropertyValues(
                "complex.obfuscators.0.mode=ALL",
                "complex.obfuscators.1.fixed-length=8", "complex.obfuscators.1.mask-char=x",
                "complex.obfuscators.2.fixed-value=<fixed>",
                "complex.obfuscators.3.mode=PROVIDER",
                "complex.nested.obfuscator.provider-class=" + TestObfuscatorProvider.class.getName()
                )
                .run(context -> assertThat(context).hasFailed());

        contextRunner.withPropertyValues(
                "complex.obfuscators.0.mode=ALL",
                "complex.obfuscators.1.fixed-length=8", "complex.obfuscators.1.mask-char=x",
                "complex.obfuscators.2.fixed-value=<fixed>",
                "complex.nested.obfuscator.mode=PROVIDER"
                )
                .run(context -> assertThat(context).hasFailed());
    }

    @Validated
    @ConfigurationProperties("complex")
    @EnableAutoConfiguration
    static class ComplexProperties {

        private List<ObfuscatorProperties> obfuscators;
        private NestedProperties nested;

        public List<ObfuscatorProperties> getObfuscators() {
            return obfuscators;
        }

        public void setObfuscators(List<ObfuscatorProperties> obfuscators) {
            this.obfuscators = obfuscators;
        }

        public NestedProperties getNested() {
            return nested;
        }

        public void setNested(NestedProperties nested) {
            this.nested = nested;
        }
    }

    static class NestedProperties {

        private ObfuscatorProperties obfuscator;
        private Map<String, ObfuscatorProperties> mapped;

        public ObfuscatorProperties getObfuscator() {
            return obfuscator;
        }

        public void setObfuscator(ObfuscatorProperties obfuscator) {
            this.obfuscator = obfuscator;
        }

        public Map<String, ObfuscatorProperties> getMapped() {
            return mapped;
        }

        public void setMapped(Map<String, ObfuscatorProperties> mapped) {
            this.mapped = mapped;
        }
    }
}
