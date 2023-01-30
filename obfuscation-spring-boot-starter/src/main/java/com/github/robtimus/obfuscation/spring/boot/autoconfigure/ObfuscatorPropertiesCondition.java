/*
 * ObfuscatorPropertiesCondition.java
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * A condition for {@link ObfuscatorProperties}. This class allows matching of {@link ObfuscatorProperties} for any prefix.
 * It will match if any of the known properties of {@link ObfuscatorProperties} is set.
 *
 * @author Rob Spoor
 */
public class ObfuscatorPropertiesCondition extends SpringBootCondition {

    @SuppressWarnings("nls")
    private static final List<String> NAMES = Collections.unmodifiableList(Arrays.asList(
            // shared
            "mode",
            "mask-char",
            // FIXED_LENGTH
            "fixed-length",
            // FIXED_VALUE
            "fixed-value",
            // PORTION
            "keep-at-start", "keep-at-end", "at-least-from-start", "at-least-from-end", "fixed-total-length",
            // PROVIDER
            "provider-class"
            ));

    private final List<String> properties;

    /**
     * Creates a new condition.
     *
     * @param prefix The property prefix to use.
     */
    @SuppressWarnings("nls")
    protected ObfuscatorPropertiesCondition(String prefix) {
        properties = NAMES.stream()
                .map(n -> prefix + "." + n)
                .toList();
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        boolean hasProperty = properties.stream()
                .anyMatch(environment::containsProperty);
        return hasProperty ? ConditionOutcome.match() : ConditionOutcome.noMatch(Messages.ObfuscatorPropertiesCondition.noMatch(properties));
    }
}
