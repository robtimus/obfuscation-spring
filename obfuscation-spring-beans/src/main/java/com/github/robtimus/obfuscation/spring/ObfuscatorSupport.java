/*
 * ObfuscatorSupport.java
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

import static com.github.robtimus.obfuscation.Obfuscator.fixedLength;
import java.lang.annotation.Annotation;
import java.util.Optional;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.CharacterRepresentationProvider;
import com.github.robtimus.obfuscation.annotation.ObjectFactory;

abstract class ObfuscatorSupport {

    static final Obfuscator DEFAULT_OBFUSCATOR = fixedLength(3);

    private final DefaultListableBeanFactory beanFactory;
    private final ObjectFactory objectFactory;

    ObfuscatorSupport(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        objectFactory = new BeanFactoryObjectFactory(beanFactory);
    }

    final DefaultListableBeanFactory beanFactory() {
        return beanFactory;
    }

    final ObjectFactory objectFactory() {
        return objectFactory;
    }

    final Optional<Obfuscator> optionalObfuscator(Annotation[] annotations) {
        return objectFactory.obfuscator(annotations);
    }

    final Obfuscator obfuscator(Annotation[] annotations) {
        return optionalObfuscator(annotations).orElseGet(this::defaultObfuscator);
    }

    private Obfuscator defaultObfuscator() {
        ObjectProvider<Obfuscator> beanProvider = beanFactory.getBeanProvider(Obfuscator.class);
        return beanProvider.getIfAvailable(() -> DEFAULT_OBFUSCATOR);
    }

    private CharacterRepresentationProvider getCharacterRepresentationProvider(Annotation[] annotations, Class<?> type) {
        return objectFactory().characterRepresentationProvider(annotations)
                .orElseGet(() -> CharacterRepresentationProvider.getDefaultInstance(type));
    }

    @SuppressWarnings("unchecked")
    final <T> T obfuscateValue(Object unobfuscatedValue, Obfuscator obfuscator, Annotation[] annotations, Class<?> type) {
        CharacterRepresentationProvider characterRepresentationProvider = getCharacterRepresentationProvider(annotations, type);
        return (T) obfuscator.obfuscateObject(unobfuscatedValue, () -> characterRepresentationProvider.toCharSequence(unobfuscatedValue));
    }
}
