/*
 * ObfuscatorSupportBeanFactoryPostProcessor.java
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscateAll;
import com.github.robtimus.obfuscation.annotation.ObfuscateFixedLength;
import com.github.robtimus.obfuscation.annotation.ObfuscateUsing;

/**
 * A {@link BeanFactoryPostProcessor} that adds support for autowiring {@link Obfuscator} instances.
 * <p>
 * If an instance of this class is activated, it will allow instances of {@link Obfuscator} to be autowired.
 * The type of {@link Obfuscator} is determined as follows:
 * <ul>
 * <li>If the {@link Obfuscator} field, constructor argument or method argument is annotated with an {@link Obfuscator} annotation like
 *     {@link ObfuscateAll}, {@link ObfuscateFixedLength} or {@link ObfuscateUsing}, the autowired {@link Obfuscator} will match the annotation.</li>
 * <li>Otherwise, if a bean of type {@link Obfuscator} is available, that will be autowired.</li>
 * <li>Otherwise, a default {@link Obfuscator} will be autowired. This will be the result of calling
 *     {@link Obfuscator#fixedLength(int) Obfuscator.fixedLength(3)}.</li>
 * </ul>
 *
 * @author Rob Spoor
 */
public class ObfuscatorSupportBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
            listableBeanFactory.setAutowireCandidateResolver(new ObfuscatorAutowireCandidateResolver(listableBeanFactory));
        }
    }

    private static final class ObfuscatorAutowireCandidateResolver extends ObfuscatorSupport implements AutowireCandidateResolver {

        private final AutowireCandidateResolver delegate;

        private ObfuscatorAutowireCandidateResolver(DefaultListableBeanFactory beanFactory) {
            this(beanFactory.getAutowireCandidateResolver(), beanFactory);
        }

        private ObfuscatorAutowireCandidateResolver(AutowireCandidateResolver delegate, DefaultListableBeanFactory beanFactory) {
            super(beanFactory);
            this.delegate = delegate;
        }

        @Override
        public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
            return delegate.isAutowireCandidate(bdHolder, descriptor);
        }

        @Override
        public boolean isRequired(DependencyDescriptor descriptor) {
            return delegate.isRequired(descriptor);
        }

        @Override
        public boolean hasQualifier(DependencyDescriptor descriptor) {
            return delegate.hasQualifier(descriptor);
        }

        @Override
        public Object getSuggestedValue(DependencyDescriptor descriptor) {
            if (Obfuscator.class.isAssignableFrom(descriptor.getDeclaredType())) {
                return obfuscator(descriptor.getAnnotations());
            }
            return delegate.getSuggestedValue(descriptor);
        }

        @Override
        public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
            return delegate.getLazyResolutionProxyIfNecessary(descriptor, beanName);
        }

        @Override
        public AutowireCandidateResolver cloneIfNecessary() {
            AutowireCandidateResolver clonedDelegate = delegate.cloneIfNecessary();
            return clonedDelegate == delegate ? this : new ObfuscatorAutowireCandidateResolver(clonedDelegate, beanFactory());
        }
    }
}
