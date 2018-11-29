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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Properties implements Cloneable, Propertable {
    private final List<Property> properties = new ArrayList<>();

    @Override
    public void appendProperties(Iterable<Property> append) {
        if (append != null) {
            for (Property toAppend : append) {
                if (this.property(toAppend.getName()) == null) {
                    this.properties.add(toAppend);
                }
            }
        }
    }

    @Override
    public Properties clone() {
        try {
            Properties clone = (Properties) super.clone();
            for (Property original : this.properties) {
                clone.properties.add(original.clone());
            }

            return clone;
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    @Override
    public boolean hasProperties() {
        return !this.properties.isEmpty();
    }

    @Override
    public List<Property> properties() {
        return new ArrayList<>(this.properties);
    }

    @Override
    public List<Property> properties(Iterable<String> names) {
        List<Property> properties = new ArrayList<>();

        if (names != null) {
            for (String name : names) {
                if (name != null) {
                    for (Property property : this.properties) {
                        if (property.getName().equals(name)) {
                            properties.add(property);
                        }
                    }
                }
            }
        }

        return properties;
    }

    @Override
    public Property property(Iterable<String> names) {
        if (names != null) {
            for (String name : names) {
                if (name != null) {
                    for (Property property : this.properties) {
                        if (property.getName().equals(name)) {
                            return property;
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public String propertyValue(Iterable<String> names) {
        return this.propertyValue(names, null);
    }

    @Override
    public String propertyValue(Iterable<String> names, String def) {
        Property property = this.property(names);
        if (property != null) {
            String value = property.getValue();

            if (value != null) {
                return value;
            }
        }

        return def;
    }

    @Override
    public Property setProperty(Property property) {
        Property oldProperty = null;
        if (property != null) {
            oldProperty = this.property(property.getName());

            if (oldProperty != null) {
                oldProperty.setValue(property.getValue());
            } else {
                this.properties.add(property);
            }
        }

        return oldProperty;
    }

    @Override
    public void setProperties(Iterable<Property> properties) {
        if (properties != null) {
            for (Property property : properties) {
                this.setProperty(property);
            }
        }
    }

    @Override
    public void sortProperties(Comparator<? super Property> comparator) {
        if (comparator != null) {
            this.properties.sort(comparator);
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(this.properties, " ");
    }

    @Override
    public int unsetProperties() {
        int count = this.properties.size();
        this.properties.clear();
        return count;
    }

    @Override
    public boolean unsetProperty(Property property) {
        return property != null && this.properties.remove(property);
    }

    @Override
    public boolean unsetProperty(String name) {
        return name != null && this.unsetProperty(this.property(name));
    }
}
