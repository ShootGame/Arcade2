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

public final class ImmutableProperty extends Property {
    protected ImmutableProperty(Namespace namespace, String name, String value) {
        super(namespace, name, value);
    }

    @Override
    public Property clone() {
        return this;
    }

    @Override
    public boolean detach() {
        return false;
    }

    @Override
    public void locate(Cursor start, Cursor end) {
    }

    @Override
    public void locate(Selection selection) {
    }

    @Override
    public String setName(String name) {
        return null;
    }

    @Override
    public Node setParent(Node parent) {
        return null;
    }

    @Override
    public String setValue(String value) {
        return null;
    }

    //
    // Instancing
    //

    public static ImmutableProperty of(Namespace namespace, String name) {
        return of(namespace, name, null);
    }

    public static ImmutableProperty of(Namespace namespace, String name, String value) {
        return new ImmutableProperty(namespace, name, value);
    }
}
