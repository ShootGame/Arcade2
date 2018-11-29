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

package pl.themolka.arcade.util;

import java.util.Map;

/**
 * Something that holds a name and a value.
 */
public interface INamedValue<K, V> extends Map.Entry<K, V> {
    /**
     * @deprecated Use {@link #getName()} instead.
     */
    @Override
    @Deprecated
    default K getKey() {
        return this.getName();
    }

    K getName();

    @Override
    V getValue();

    boolean hasValue();

    K setName(K name);

    @Override
    V setValue(V value);
}
