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

import pl.themolka.arcade.util.NamedValue;

/**
 * Something that can hold a parent, name, value and its location.
 * This is the base class for all DOM representation classes.
 */
public abstract class Element extends NamedValue<String, String>
                              implements Cloneable, NestedContent<Node> {
    private final Namespace namespace;
    
    public Element(Namespace namespace, String name) {
        super(name);
        this.namespace = namespace != null ? namespace : Namespace.getDefault();
    }

    public Element(Namespace namespace, String name, String value) {
        super(name, value);
        this.namespace = namespace != null ? namespace : Namespace.getDefault();
    }

    @Override
    public Element clone() {
        return (Element) super.clone();
    }

    public Namespace getNamespace() {
        return this.namespace;
    }
}
