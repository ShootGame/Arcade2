/*
 * Copyright 2018 Aleksander Jagiełło
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

package pl.themolka.arcade.attribute;

import java.util.Objects;

public class FixedAttributeKey extends AttributeKey {
    public static final String DEFAULT_NAMESPACE = "arcade";
    public static final String NAMESPACE_SEPARATOR = ".";

    private final String namespace;
    private final String key;

    public FixedAttributeKey(String namespace, String key) {
        this.namespace = requireNamespace(namespace);
        this.key = requireKey(key);
    }

    @Override
    public String key() {
        return computeKey(this.namespace, this.key);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getKey() {
        return this.key;
    }

    public static String computeKey(String key) {
        return computeKey(DEFAULT_NAMESPACE, key);
    }

    public static String computeKey(String namespace, String key) {
        return computeKey(namespace, NAMESPACE_SEPARATOR, key);
    }

    public static String computeKey(String namespace, String separator, String key) {
        return requireNamespace(namespace) + Objects.requireNonNull(separator, "separator cannot be null") + requireKey(key);
    }

    private static String requireNamespace(String namespace) {
        return Objects.requireNonNull(namespace, "namespace cannot be null");
    }

    private static String requireKey(String key) {
        return Objects.requireNonNull(key, "key cannot be null");
    }
}
