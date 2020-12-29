/*
 * ObfuscatedSupportBeanFactoryPostProcessor.java
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import com.github.robtimus.obfuscation.Obfuscated;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.CharacterRepresentationProvider;
import com.github.robtimus.obfuscation.annotation.ObfuscateAll;
import com.github.robtimus.obfuscation.annotation.ObfuscateFixedLength;
import com.github.robtimus.obfuscation.annotation.ObfuscateUsing;
import com.github.robtimus.obfuscation.annotation.RepresentedBy;

/**
 * A {@link BeanFactoryPostProcessor} that adds support for autowiring {@link Obfuscated} instances.
 * <p>
 * If an instance of this class is activated, it will allow instances of {@link Obfuscated} to be autowired.
 * This autowiring consists of three steps:
 * <ol>
 * <li>Finding the value to obfuscate. This is done using the default mechanisms. A field, constructor argument or method argument can be annotated
 *     with {@link Value}, it can be a bean, or anything else that has default autowiring support.
 * <li>Finding the obfuscator to use. The type of {@link Obfuscator} is determined as follows:
 *   <ul>
 *   <li>If the {@link Obfuscator} field, constructor argument or method argument is annotated with an {@link Obfuscator} annotation like
 *       {@link ObfuscateAll}, {@link ObfuscateFixedLength} or {@link ObfuscateUsing}, the autowired {@link Obfuscator} will match the annotation.
 *       </li>
 *   <li>Otherwise, if a bean of type {@link Obfuscator} is available, that will be autowired.</li>
 *   <li>Otherwise, a default {@link Obfuscator} will be autowired. This will be the result of calling
 *       {@link Obfuscator#fixedLength(int) Obfuscator.fixedLength(3)}.</li>
 *   </ul>
 *   </li>
 * <li>Finding the character representation to use. This is done by annotating the field, constructor argument or method argument with
 *     {@link RepresentedBy}. If this annotation is absent, the
 *     {@link CharacterRepresentationProvider#getDefaultInstance(Class) default character representation} is used.</li>
 * </ol>
 * <p>
 * The {@link Obfuscator} and character representation are used to wrap the value to obfuscate in an {@link Obfuscated} instance, which will then be
 * autowired.
 *
 * @author Rob Spoor
 */
public class ObfuscatedSupportBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        listableBeanFactory.setAutowireCandidateResolver(new ObfuscatedAutowireCandidateResolver(listableBeanFactory));
        listableBeanFactory.setTypeConverter(new ObfuscatedTypeConverter(listableBeanFactory));
    }

    private static final class ObfuscatedAutowireCandidateResolver extends ObfuscatorSupport implements AutowireCandidateResolver {

        private final AutowireCandidateResolver delegate;

        private ObfuscatedAutowireCandidateResolver(DefaultListableBeanFactory beanFactory) {
            this(beanFactory.getAutowireCandidateResolver(), beanFactory);
        }

        private ObfuscatedAutowireCandidateResolver(AutowireCandidateResolver delegate, DefaultListableBeanFactory beanFactory) {
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
            if (Obfuscated.class.isAssignableFrom(descriptor.getDependencyType())) {
                DependencyDescriptor genericDescriptor = genericType(descriptor);

                // Delegate not to delegate but beanFactory's actual autowire candidate resolver.
                // This allows this autowire candidate resolver to be nested in another delegating autowire candidate resolver implementation
                // and use the delegating implementations.
                // This will not trigger this block again due to the different dependency descriptor
                Object result = beanFactory().getAutowireCandidateResolver().getSuggestedValue(genericDescriptor);
                if (result == null) {
                    result = beanFactory().getBeanProvider(genericDescriptor.getDependencyType()).getIfAvailable();
                }
                // Do not wrap in Obfuscated just yet, as that will prevent any possible @Value resolving
                // Instead, let the TypeConverter convert the value instead.
                return result;
            }
            return delegate.getSuggestedValue(descriptor);
        }

        @Override
        public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
            if (Obfuscated.class.isAssignableFrom(descriptor.getDependencyType())) {
                DependencyDescriptor genericDescriptor = genericType(descriptor);

                Object result = beanFactory().getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(genericDescriptor, beanName);
                if (result != null) {
                    Annotation[] annotations = descriptor.getAnnotations();
                    Obfuscator obfuscator = obfuscator(annotations);

                    result = obfuscateValue(result, obfuscator, annotations, genericDescriptor.getResolvableType().getRawClass());
                }
                return result;
            }
            return delegate.getLazyResolutionProxyIfNecessary(descriptor, beanName);
        }

        @Override
        public AutowireCandidateResolver cloneIfNecessary() {
            AutowireCandidateResolver clonedDelegate = delegate.cloneIfNecessary();
            return clonedDelegate == delegate ? this : new ObfuscatedAutowireCandidateResolver(clonedDelegate, beanFactory());
        }

        private DependencyDescriptor genericType(DependencyDescriptor descriptor) {
            DependencyDescriptor genericDescriptor = new DependencyDescriptor(descriptor);
            genericDescriptor.increaseNestingLevel();
            return genericDescriptor;
        }
    }

    private static final class ObfuscatedTypeConverter extends ObfuscatorSupport implements TypeConverter {

        private final TypeConverter delegate;

        private ObfuscatedTypeConverter(DefaultListableBeanFactory beanFactory) {
            super(beanFactory);
            this.delegate = beanFactory.getTypeConverter();
        }

        @Override
        public <T> T convertIfNecessary(Object value, Class<T> requiredType) {
            return delegate.convertIfNecessary(value, requiredType);
        }

        @Override
        public <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field) {
            if (needsConversion(value, requiredType)) {
                Annotation[] annotations = field.getAnnotations();
                Obfuscator obfuscator = obfuscator(annotations);

                ResolvableType genericResolvableType = getGenericType(field);
                Class<?> genericRequiredType = genericResolvableType.getRawClass();

                // Delegate not to delegate but beanFactory's actual type converter.
                // This allows this type converter to be nested in another delegating type converter implementation
                // and use the delegating implementations.
                // This will not trigger this block again due to the different required type
                Object unobfuscatedValue = beanFactory().getTypeConverter().convertIfNecessary(value, genericRequiredType, field);
                return obfuscateValue(unobfuscatedValue, obfuscator, annotations, genericRequiredType);
            }
            return delegate.convertIfNecessary(value, requiredType, field);
        }

        @Override
        public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) {
            if (needsConversion(value, requiredType)) {
                Annotation[] annotations = methodParam.getParameterAnnotations();
                Obfuscator obfuscator = obfuscator(annotations);

                ResolvableType genericResolvableType = getGenericType(methodParam);
                Class<?> genericRequiredType = genericResolvableType.getRawClass();

                // Delegate not to delegate but beanFactory's actual type converter.
                // This allows this type converter to be nested in another delegating type converter implementation
                // and use the delegating implementations.
                // This will not trigger this block again due to the different required type
                Object unobfuscatedValue = beanFactory().getTypeConverter().convertIfNecessary(value, genericRequiredType, methodParam);
                return obfuscateValue(unobfuscatedValue, obfuscator, annotations, genericRequiredType);
            }
            return delegate.convertIfNecessary(value, requiredType, methodParam);
        }

        @Override
        public <T> T convertIfNecessary(Object value, Class<T> requiredType, TypeDescriptor typeDescriptor) {
            if (needsConversion(value, requiredType)) {
                // throwing UnsupportedOperationException from the default implementation will trigger one of the other methods
                return TypeConverter.super.convertIfNecessary(value, requiredType, typeDescriptor);
            }
            return delegate.convertIfNecessary(value, requiredType, typeDescriptor);
        }

        private boolean needsConversion(Object value, Class<?> requiredType) {
            return Obfuscated.class.isAssignableFrom(requiredType) && !(value instanceof Obfuscated<?>);
        }

        private ResolvableType getGenericType(Field field) {
            TypeDescriptor typeDescriptor = new TypeDescriptor(field);
            return typeDescriptor.getResolvableType().getGeneric(0);
        }

        private ResolvableType getGenericType(MethodParameter methodParameter) {
            TypeDescriptor typeDescriptor = new TypeDescriptor(methodParameter);
            return typeDescriptor.getResolvableType().getGeneric(0);
        }
    }
}
