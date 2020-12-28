/*
 * BeanFactoryObjectFactory.java
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

import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import com.github.robtimus.obfuscation.annotation.ObjectFactory;

/**
 * An {@link ObjectFactory} that is backed by an {@link AutowireCapableBeanFactory}.
 * It will try to lookup a bean of the given type first. If none is available, a new instance is created using the backing bean factory.
 * This allows created instances to have autowired fields of their own.
 *
 * @author Rob Spoor
 */
public class BeanFactoryObjectFactory implements ObjectFactory {

    private final AutowireCapableBeanFactory beanFactory;

    /**
     * Creates a new object factory.
     *
     * @param beanFactory The backing bean factory.
     * @throws NullPointerException If the given bean factory is {@code null}.
     */
    public BeanFactoryObjectFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = Objects.requireNonNull(beanFactory);
    }

    @Override
    public <T> T instance(Class<T> type) {
        ObjectProvider<T> beanProvider = beanFactory.getBeanProvider(type);
        return beanProvider.getIfAvailable(() -> beanFactory.createBean(type));
    }
}
