/*
 * ObfuscatorProperties.java
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.validation.annotation.Validated;
import com.github.robtimus.obfuscation.Obfuscator;
import com.github.robtimus.obfuscation.annotation.ObfuscatorProvider;
import com.github.robtimus.obfuscation.annotation.ObjectFactory;
import com.github.robtimus.obfuscation.spring.BeanFactoryObjectFactory;
import com.github.robtimus.obfuscation.spring.boot.autoconfigure.ObfuscatorProperties.ValidObfuscatorProperties;

/**
 * Properties for {@link Obfuscator Obfuscators}.
 *
 * @author Rob Spoor
 */
@Validated
@ValidObfuscatorProperties
@SuppressWarnings("javadoc")
public class ObfuscatorProperties {

    private static final String FIELD_MODE = "mode"; //$NON-NLS-1$
    private static final String FIELD_MASK_CHAR = "maskChar"; //$NON-NLS-1$
    private static final String FIELD_FIXED_LENGTH = "fixedLength"; //$NON-NLS-1$
    private static final String FIELD_FIXED_VALUE = "fixedValue"; //$NON-NLS-1$
    private static final String FIELD_KEEP_AT_START = "keepAtStart"; //$NON-NLS-1$
    private static final String FIELD_KEEP_AT_END = "keepAtEnd"; //$NON-NLS-1$
    private static final String FIELD_AT_LEAST_FROM_START = "atLeastFromStart"; //$NON-NLS-1$
    private static final String FIELD_AT_LEAST_FROM_END = "atLeastFromEnd"; //$NON-NLS-1$
    private static final String FIELD_FIXED_TOTAL_LENGTH = "fixedTotalLength"; //$NON-NLS-1$
    private static final String FIELD_PROVIDER_CLASS = "providerClass"; //$NON-NLS-1$

    /**
     * The obfuscation mode.
     * Only necessary to specify {@link ObfuscationMode#ALL} or {@link ObfuscationMode#NONE}, other values can be automatically determined based on
     * other settings.
     */
    private ObfuscationMode mode;

    /**
     * The mask character to use.
     * Forbidden if the mode is {@link ObfuscationMode#NONE}, {@link ObfuscationMode#FIXED_VALUE} or {@link ObfuscationMode#PROVIDER}; otherwise it
     * defaults to {@code *}.
     */
    private Character maskChar;

    /**
     * The fixed length to use.
     * Implies {@link ObfuscationMode#FIXED_LENGTH}; forbidden for other modes.
     */
    @Min(0)
    private Integer fixedLength;

    /**
     * The fixed value to use.
     * Implies {@link ObfuscationMode#FIXED_VALUE}; forbidden for other modes.
     */
    private String fixedValue;

    /**
     * The number of characters at the start that created obfuscators will skip when obfuscating.
     * Implies {@link ObfuscationMode#PORTION}; forbidden for other modes.
     */
    @Min(0)
    private Integer keepAtStart;

    /**
     * The number of characters at the end that created obfuscators will skip when obfuscating.
     * Implies {@link ObfuscationMode#PORTION}; forbidden for other modes.
     */
    @Min(0)
    private Integer keepAtEnd;

    /**
     * The minimum number of characters from the start that need to be obfuscated.
     * Implies {@link ObfuscationMode#PORTION}; forbidden for other modes.
     */
    @Min(0)
    private Integer atLeastFromStart;

    /**
     * The minimum number of characters from the end that need to be obfuscated.
     * Implies {@link ObfuscationMode#PORTION}; forbidden for other modes.
     */
    @Min(0)
    private Integer atLeastFromEnd;

    /**
     * The fixed total length to use for obfuscated contents.
     * Implies {@link ObfuscationMode#PORTION}; forbidden for other modes.
     */
    // Negative is allowed, to use the actual length
    private Integer fixedTotalLength;

    /**
     * The {@link ObfuscatorProvider} class to use.
     * Implies {@link ObfuscationMode#PROVIDER}; forbidden for other modes.
     */
    private Class<? extends ObfuscatorProvider> providerClass;

    public ObfuscationMode getMode() {
        return mode;
    }

    public void setMode(ObfuscationMode mode) {
        this.mode = mode;
    }

    public Character getMaskChar() {
        return maskChar;
    }

    public void setMaskChar(Character maskChar) {
        this.maskChar = maskChar;
    }

    public Integer getFixedLength() {
        return fixedLength;
    }

    public void setFixedLength(Integer fixedLength) {
        this.fixedLength = fixedLength;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public Integer getKeepAtStart() {
        return keepAtStart;
    }

    public void setKeepAtStart(Integer keepAtStart) {
        this.keepAtStart = keepAtStart;
    }

    public Integer getKeepAtEnd() {
        return keepAtEnd;
    }

    public void setKeepAtEnd(Integer keepAtEnd) {
        this.keepAtEnd = keepAtEnd;
    }

    public Integer getAtLeastFromStart() {
        return atLeastFromStart;
    }

    public void setAtLeastFromStart(Integer atLeastFromStart) {
        this.atLeastFromStart = atLeastFromStart;
    }

    public Integer getAtLeastFromEnd() {
        return atLeastFromEnd;
    }

    public void setAtLeastFromEnd(Integer atLeastFromEnd) {
        this.atLeastFromEnd = atLeastFromEnd;
    }

    public Integer getFixedTotalLength() {
        return fixedTotalLength;
    }

    public void setFixedTotalLength(Integer fixedTotalLength) {
        this.fixedTotalLength = fixedTotalLength;
    }

    public Class<? extends ObfuscatorProvider> getProviderClass() {
        return providerClass;
    }

    public void setProviderClass(Class<? extends ObfuscatorProvider> providerClass) {
        // When called from Java code, the provider class should be safe
        // However, when the class is set through application properties the type cannot be verified
        // This check will cause an error to be thrown early on
        if (providerClass != null) {
            providerClass.asSubclass(ObfuscatorProvider.class);
        }
        this.providerClass = providerClass;
    }

    /**
     * Creates an obfuscator based on the properties configured in this object.
     *
     * @param beanFactory A bean factory to use to create instances of {@link ObfuscatorProvider} if needed.
     * @return The created obfuscator.
     * @throws NullPointerException If the given bean factory is {@code null}.
     * @throws IllegalStateException If this object is in an inconsistent state.
     */
    public Obfuscator createObfuscator(AutowireCapableBeanFactory beanFactory) {
        ObjectFactory objectFactory = new BeanFactoryObjectFactory(beanFactory);
        return createObfuscator(objectFactory);
    }

    /**
     * Creates an obfuscator based on the properties configured in this object.
     *
     * @param objectFactory The object factory to use to create instances of {@link ObfuscatorProvider} if needed.
     * @return The created obfuscator.
     * @throws NullPointerException If the given object factory is {@code null}.
     * @throws IllegalStateException If this object is in an inconsistent state.
     */
    public Obfuscator createObfuscator(ObjectFactory objectFactory) {
        Objects.requireNonNull(objectFactory);
        ObfuscationMode obfuscationMode = determineObfuscationMode();
        return obfuscationMode.factory.apply(this, objectFactory);
    }

    /**
     * Creates obfuscators based on the properties configured in multiple properties objects.
     *
     * @param properties A collection of properties objects for which to create obfuscators.
     * @param beanFactory A bean factory to use to create instances of {@link ObfuscatorProvider} if needed.
     * @return The created obfuscators.
     * @throws NullPointerException If the given collection, any of its elements, or the given bean factory is {@code null}.
     * @throws IllegalStateException If any of the properties objects is in an inconsistent state.
     */
    public static List<Obfuscator> createObfuscators(Collection<ObfuscatorProperties> properties, AutowireCapableBeanFactory beanFactory) {
        ObjectFactory objectFactory = new BeanFactoryObjectFactory(beanFactory);
        return createObfuscators(properties, objectFactory);
    }

    /**
     * Creates obfuscators based on the properties configured in multiple properties objects.
     *
     * @param properties A collection of properties objects for which to create obfuscators.
     * @param objectFactory The object factory to use to create instances of {@link ObfuscatorProvider} if needed.
     * @return The created obfuscators.
     * @throws NullPointerException If the given collection, any of its elements, or the given bean factory is {@code null}.
     * @throws IllegalStateException If any of the properties objects is in an inconsistent state.
     */
    public static List<Obfuscator> createObfuscators(Collection<ObfuscatorProperties> properties, ObjectFactory objectFactory) {
        Objects.requireNonNull(objectFactory);
        return properties.stream()
                .map(p -> p.createObfuscator(objectFactory))
                .toList();
    }

    /**
     * Creates obfuscators based on the properties configured in multiple properties objects.
     *
     * @param <K> The map key type.
     * @param properties A map with properties objects for which to create obfuscators.
     * @param beanFactory A bean factory to use to create instances of {@link ObfuscatorProvider} if needed.
     * @return The created obfuscators.
     * @throws NullPointerException If the given map, any of its values, or the given bean factory is {@code null}.
     * @throws IllegalStateException If any of the properties objects is in an inconsistent state.
     * @since 2.2
     */
    public static <K> Map<K, Obfuscator> createObfuscators(Map<? extends K, ObfuscatorProperties> properties,
            AutowireCapableBeanFactory beanFactory) {

        ObjectFactory objectFactory = new BeanFactoryObjectFactory(beanFactory);
        return createObfuscators(properties, objectFactory);
    }

    /**
     * Creates obfuscators based on the properties configured in multiple properties objects.
     *
     * @param <K> The map key type.
     * @param properties A map with properties objects for which to create obfuscators.
     * @param objectFactory The object factory to use to create instances of {@link ObfuscatorProvider} if needed.
     * @return The created obfuscators.
     * @throws NullPointerException If the given map, any of its values, or the given bean factory is {@code null}.
     * @throws IllegalStateException If any of the properties objects is in an inconsistent state.
     * @since 2.2
     */
    public static <K> Map<K, Obfuscator> createObfuscators(Map<? extends K, ObfuscatorProperties> properties, ObjectFactory objectFactory) {
        Objects.requireNonNull(objectFactory);
        return properties.entrySet()
                .stream()
                // The map's own keys are used as map keys, so no risk of IllegalStateExceptions
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().createObfuscator(objectFactory)));
    }

    ObfuscationMode determineObfuscationMode() {
        if (mode != null) {
            return mode;
        }
        Set<ObfuscationMode> obfuscationModes = determineObfuscationModes();
        if (obfuscationModes.isEmpty()) {
            throw new IllegalStateException(Messages.ObfuscatorProperties.noObfuscationModes());
        }
        if (obfuscationModes.size() > 1) {
            throw new IllegalStateException(Messages.ObfuscatorProperties.multipleObfuscationModes());
        }
        return obfuscationModes.iterator().next();
    }

    private Set<ObfuscationMode> determineObfuscationModes() {
        if (mode != null) {
            return Collections.singleton(mode);
        }
        Set<ObfuscationMode> obfuscationModes = EnumSet.noneOf(ObfuscationMode.class);
        if (fixedLength != null) {
            obfuscationModes.add(ObfuscationMode.FIXED_LENGTH);
        }
        if (fixedValue != null) {
            obfuscationModes.add(ObfuscationMode.FIXED_VALUE);
        }
        if (keepAtStart != null || keepAtEnd != null || atLeastFromStart != null || atLeastFromEnd != null || fixedTotalLength != null) {
            obfuscationModes.add(ObfuscationMode.PORTION);
        }
        if (providerClass != null) {
            obfuscationModes.add(ObfuscationMode.PROVIDER);
        }
        return obfuscationModes;
    }

    private char maskChar() {
        return maskChar != null ? maskChar : '*';
    }

    private int fixedLength() {
        if (fixedLength == null) {
            throw new IllegalStateException(Messages.ObfuscatorProperties.missingProperty(FIELD_FIXED_LENGTH));
        }
        return fixedLength;
    }

    private String fixedValue() {
        if (fixedValue == null) {
            throw new IllegalStateException(Messages.ObfuscatorProperties.missingProperty(FIELD_FIXED_VALUE));
        }
        return fixedValue;
    }

    private Obfuscator createPortionObfuscator() {
        return Obfuscator.portion()
                .keepAtStart(keepAtStart != null ? keepAtStart : 0)
                .keepAtEnd(keepAtEnd != null ? keepAtEnd : 0)
                .atLeastFromStart(atLeastFromStart != null ? atLeastFromStart : 0)
                .atLeastFromEnd(atLeastFromEnd != null ? atLeastFromEnd : 0)
                .withFixedTotalLength(fixedTotalLength != null ? fixedTotalLength : -1)
                .withMaskChar(maskChar())
                .build();
    }

    private Obfuscator createObfuscatorFromProvider(ObjectFactory objectFactory) {
        Class<? extends ObfuscatorProvider> obfuscatorProviderClass = obfuscatorProviderClass();
        ObfuscatorProvider obfuscatorProvider = objectFactory.obfuscatorProvider(obfuscatorProviderClass);
        return obfuscatorProvider.obfuscator();
    }

    private Class<? extends ObfuscatorProvider> obfuscatorProviderClass() {
        if (providerClass == null) {
            throw new IllegalStateException(Messages.ObfuscatorProperties.missingProperty(FIELD_PROVIDER_CLASS));
        }
        return providerClass;
    }

    /**
     * The supported obfuscation modes.
     *
     * @author Rob Spoor
     */
    public enum ObfuscationMode {
        /** Indicates {@link Obfuscator#all(char)} should be used. */
        ALL((p, f) -> Obfuscator.all(p.maskChar()), ObfuscatorPropertiesValidator::isValidForAll),

        /** Indicates {@link Obfuscator#none()} should be used. */
        NONE((p, f) -> Obfuscator.none(), ObfuscatorPropertiesValidator::isValidForNone),

        /** Indicates {@link Obfuscator#fixedLength(int, char)} should be used. */
        FIXED_LENGTH((p, f) -> Obfuscator.fixedLength(p.fixedLength(), p.maskChar()), ObfuscatorPropertiesValidator::isValidForFixedLength),

        /** Indicates {@link Obfuscator#fixedValue(String)} should be used. */
        FIXED_VALUE((p, f) -> Obfuscator.fixedValue(p.fixedValue()), ObfuscatorPropertiesValidator::isValidForFixedValue),

        /** Indicates {@link Obfuscator#portion()} should be used. */
        PORTION((p, f) -> p.createPortionObfuscator(), ObfuscatorPropertiesValidator::isValidForPortion),

        /** Indicates a custom {@link ObfuscatorProvider} should be used. */
        PROVIDER((p, f) -> p.createObfuscatorFromProvider(f), ObfuscatorPropertiesValidator::isValidForClass),
        ;

        private final BiFunction<ObfuscatorProperties, ObjectFactory, Obfuscator> factory;
        private final BiPredicate<ObfuscatorProperties, ConstraintValidatorContext> validator;

        ObfuscationMode(BiFunction<ObfuscatorProperties, ObjectFactory, Obfuscator> factory,
                BiPredicate<ObfuscatorProperties, ConstraintValidatorContext> validator) {

            this.factory = factory;
            this.validator = validator;
        }
    }

    /**
     * Indicates an {@link ObfuscatorProperties} object must be valid.
     *
     * @author Rob Spoor
     */
    @Documented
    @Constraint(validatedBy = ObfuscatorPropertiesValidator.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidObfuscatorProperties {

        // This message will never be used; the default violation will be disabled and overridden per property
        String message() default "invalid obfuscator properties";

        Class<?>[] groups() default { };

        Class<? extends Payload>[] payload() default { };
    }

    /**
     * Validator for {@link ValidObfuscatorProperties} and {@link ObfuscatorProperties}.
     *
     * @author Rob Spoor
     */
    public static final class ObfuscatorPropertiesValidator implements ConstraintValidator<ValidObfuscatorProperties, ObfuscatorProperties> {

        @Override
        public boolean isValid(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();

            Set<ObfuscationMode> obfuscationModes = properties.determineObfuscationModes();
            if (obfuscationModes.isEmpty()) {
                context.buildConstraintViolationWithTemplate(Messages.ObfuscatorProperties.noObfuscationModes())
                        .addPropertyNode(FIELD_MODE)
                        .addConstraintViolation();
                return false;
            }
            if (obfuscationModes.size() > 1) {
                context.buildConstraintViolationWithTemplate(Messages.ObfuscatorProperties.multipleObfuscationModes())
                        .addPropertyNode(FIELD_MODE)
                        .addConstraintViolation();
                return false;
            }
            ObfuscationMode obfuscationMode = obfuscationModes.iterator().next();
            return obfuscationMode.validator.test(properties, context);
        }

        private static boolean isValidForAll(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            // No required properties
            // Forbidden properties:
            valid &= fixedLengthNotSet(properties, context);
            valid &= fixedValueNotSet(properties, context);
            valid &= portionFieldsNotSet(properties, context);
            valid &= providerClassNotSet(properties, context);
            return valid;
        }

        private static boolean isValidForNone(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            // No required properties
            // Forbidden properties:
            valid &= maskCharNotSet(properties, context);
            valid &= fixedLengthNotSet(properties, context);
            valid &= fixedValueNotSet(properties, context);
            valid &= portionFieldsNotSet(properties, context);
            valid &= providerClassNotSet(properties, context);
            return valid;
        }

        private static boolean isValidForFixedLength(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            // Required properties:
            valid &= fixedLengthSet(properties, context);
            // Forbidden properties:
            valid &= fixedValueNotSet(properties, context);
            valid &= portionFieldsNotSet(properties, context);
            valid &= providerClassNotSet(properties, context);
            return valid;
        }

        private static boolean isValidForFixedValue(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            // Required properties:
            valid &= fixedValueSet(properties, context);
            // Forbidden properties:
            valid &= maskCharNotSet(properties, context);
            valid &= fixedLengthNotSet(properties, context);
            valid &= portionFieldsNotSet(properties, context);
            valid &= providerClassNotSet(properties, context);
            return valid;
        }

        private static boolean isValidForPortion(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            // No required properties
            // Forbidden properties:
            valid &= fixedLengthNotSet(properties, context);
            valid &= fixedValueNotSet(properties, context);
            valid &= providerClassNotSet(properties, context);
            return valid;
        }

        private static boolean isValidForClass(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            // Required properties:
            valid &= providerClassSet(properties, context);
            // Forbidden properties:
            valid &= maskCharNotSet(properties, context);
            valid &= fixedLengthNotSet(properties, context);
            valid &= fixedValueNotSet(properties, context);
            valid &= portionFieldsNotSet(properties, context);
            return valid;
        }

        // Forbidden checks

        private static boolean maskCharNotSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isNotSet(properties.getMaskChar(), FIELD_MASK_CHAR, context);
        }

        private static boolean fixedLengthNotSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isNotSet(properties.getFixedLength(), FIELD_FIXED_LENGTH, context);
        }

        private static boolean fixedValueNotSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isNotSet(properties.getFixedValue(), FIELD_FIXED_VALUE, context);
        }

        private static boolean portionFieldsNotSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            boolean valid = true;
            valid &= isNotSet(properties.getKeepAtStart(), FIELD_KEEP_AT_START, context);
            valid &= isNotSet(properties.getKeepAtEnd(), FIELD_KEEP_AT_END, context);
            valid &= isNotSet(properties.getAtLeastFromStart(), FIELD_AT_LEAST_FROM_START, context);
            valid &= isNotSet(properties.getAtLeastFromEnd(), FIELD_AT_LEAST_FROM_END, context);
            valid &= isNotSet(properties.getFixedTotalLength(), FIELD_FIXED_TOTAL_LENGTH, context);
            return valid;
        }

        private static boolean providerClassNotSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isNotSet(properties.getProviderClass(), FIELD_PROVIDER_CLASS, context);
        }

        private static boolean isNotSet(Object value, String fieldName, ConstraintValidatorContext context) {
            if (value != null) {
                context.buildConstraintViolationWithTemplate("{jakarta.validation.constraints.Null.message}") //$NON-NLS-1$
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
                return false;
            }
            return true;
        }

        // Required checks

        private static boolean fixedLengthSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isSet(properties.getFixedLength(), FIELD_FIXED_LENGTH, context);
        }

        private static boolean fixedValueSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isSet(properties.getFixedValue(), FIELD_FIXED_VALUE, context);
        }

        private static boolean providerClassSet(ObfuscatorProperties properties, ConstraintValidatorContext context) {
            return isSet(properties.getProviderClass(), FIELD_PROVIDER_CLASS, context);
        }

        private static boolean isSet(Object value, String fieldName, ConstraintValidatorContext context) {
            if (value == null) {
                context.buildConstraintViolationWithTemplate("{jakarta.validation.constraints.NotNull.message}") //$NON-NLS-1$
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
    }
}
