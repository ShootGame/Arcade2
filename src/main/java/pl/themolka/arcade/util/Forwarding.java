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

import com.google.common.collect.ForwardingObject;

import java.util.Objects;

/**
 * The {@link ForwardingObject} does not support generics... The best way to
 * omit this is to create our own class with will use generics.
 * @param <T> The forwarded class type.
 */
public abstract class Forwarding<T> extends ForwardingObject {
    @Override
    protected abstract T delegate();

    @Override
    public String toString() {
        return Objects.toString(this.delegate());
    }
}
