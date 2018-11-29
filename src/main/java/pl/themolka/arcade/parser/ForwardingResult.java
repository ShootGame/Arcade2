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

package pl.themolka.arcade.parser;

import pl.themolka.arcade.util.Forwarding;

import java.util.Optional;

public abstract class ForwardingResult<T> extends Forwarding<IResult<T>>
                                          implements IResult<T> {
    @Override
    public T get() {
        return this.delegate().get();
    }

    @Override
    public boolean isPresent() {
        return this.delegate().isPresent();
    }

    @Override
    public Optional<T> optional() {
        return this.delegate().optional();
    }

    @Override
    public T or(T def) {
        return this.delegate().or(def);
    }

    @Override
    public T orDefault(T def) throws ParserException {
        return this.delegate().orDefault(def);
    }

    @Override
    public T orDefaultNull() throws ParserException {
        return this.delegate().orDefaultNull();
    }

    @Override
    public T orFail() throws ParserException {
        return this.delegate().orFail();
    }

    @Override
    public T orNull() {
        return this.delegate().orNull();
    }
}
