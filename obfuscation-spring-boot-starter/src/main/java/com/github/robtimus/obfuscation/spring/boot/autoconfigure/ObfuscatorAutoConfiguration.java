/*
 * ObfuscatorAutoConfiguration.java
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

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.spring.boot.autoconfigure.ObfuscatorAutoConfiguration.DefaultObfuscatorCondition;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link Obfuscator}.
 *
 * @author Rob Spoor
 */
@Configuration
@ConditionalOnMissingBean(Obfuscator.class)
@Conditional(DefaultObfuscatorCondition.class)
@EnableConfigurationProperties
@SuppressWarnings("javadoc")
public class ObfuscatorAutoConfiguration {

    @Bean
    @ConfigurationProperties("obfuscation.default-obfuscator")
    public ObfuscatorProperties defaultObfuscatorProperties() {
        return new ObfuscatorProperties();
    }

    @Bean
    public Obfuscator defaultObfuscator(AutowireCapableBeanFactory beanFactory) {
        ObfuscatorProperties properties = defaultObfuscatorProperties();
        return properties.createObfuscator(beanFactory);
    }

    static final class DefaultObfuscatorCondition extends ObfuscatorPropertiesCondition {

        private DefaultObfuscatorCondition() {
            super("obfuscation.default-obfuscator"); //$NON-NLS-1$
        }
    }
}
