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

package pl.themolka.arcade.dom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Something that can hold properties.
 */
public interface Propertable {
    void appendProperties(Iterable<Property> append);

    default void appendProperties(Property... append) {
        if (append != null) {
            this.appendProperties(Arrays.asList(append));
        }
    }

    boolean hasProperties();

    List<Property> properties();

    List<Property> properties(Iterable<String> names);

    default List<Property> properties(String... names) {
        return names != null ? this.properties(Arrays.asList(names))
                             : null;
    }

    Property property(Iterable<String> names);

    default Property property(String... names) {
        return names != null ? this.property(Arrays.asList(names))
                             : null;
    }

    String propertyValue(Iterable<String> names);

    default String propertyValue(String... names) {
        return names != null ? this.propertyValue(Arrays.asList(names))
                             : null;
    }

    String propertyValue(Iterable<String> names, String def);

    Property setProperty(Property property);

    default Property setProperty(Namespace namespace, String name, String value) {
        return name != null ? this.setProperty(Property.of(namespace, name, value))
                            : null;
    }

    void setProperties(Iterable<Property> properties);

    default void setProperties(Property... properties) {
        if (properties != null) {
            this.setProperties(Arrays.asList(properties));
        }
    }

    void sortProperties(Comparator<? super Property> comparator);

    int unsetProperties();

    boolean unsetProperty(Property property);

    boolean unsetProperty(String name);
}
