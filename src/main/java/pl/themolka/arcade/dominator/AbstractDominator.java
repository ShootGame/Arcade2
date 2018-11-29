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

package pl.themolka.arcade.dominator;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractDominator<T> implements Dominator<T> {
    protected Map<T, Integer> empty() {
        return Collections.EMPTY_MAP;
    }

    protected Map<T, Integer> singleton(Map.Entry<T, Integer> dominator) {
        return this.singleton(dominator.getKey(), dominator.getValue());
    }

    protected Map<T, Integer> singleton(T t, Integer power) {
        return ImmutableMap.of(t, power);
    }
}
