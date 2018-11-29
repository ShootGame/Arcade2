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

package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.config.Ref;

import java.util.Objects;

public abstract class ConfigurableMatcher<T> extends Matcher<T> {
    private final T value;

    public ConfigurableMatcher(T value) {
        this.value = value;
    }

    @Override
    public boolean matches(T t) {
        return Objects.equals(this.value, t);
    }

    public T getValue() {
        return this.value;
    }

    public interface Config<T extends ConfigurableMatcher<?>, R> extends Matcher.Config<T> {
        Ref<R> value();
    }
}
