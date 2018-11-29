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

package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Child;
import pl.themolka.arcade.util.StringId;

public class SimpleConfig<T extends SimpleConfig<T>> implements Child<T>, IConfig, StringId {
    protected final String id;
    protected final T parent;

    public SimpleConfig(String id) {
        this(id, null);
    }

    public SimpleConfig(String id, T parent) {
        this.id = id;
        this.parent = parent;
    }

    @Override
    public final boolean detach() {
        return false;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }
}
