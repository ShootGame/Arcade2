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

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class BaseNestedParser<T> extends NodeParser<T> {
    private final List<String> names;

    public BaseNestedParser() {
        NestedParserName name = this.getClass().getDeclaredAnnotation(NestedParserName.class);
        if (name == null) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                    " must be @" + NestedParserName.class.getSimpleName() + " decorated!");
        }

        if (ArrayUtils.isEmpty(name.value())) {
            throw new UnsupportedOperationException("No parser name provided.");
        }

        this.names = ImmutableList.<String>builder().add(name.value()).build();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton(this.names.get(0) + " " + this.expectType());
    }

    protected abstract String expectType();

    public List<String> names() {
        return this.names;
    }
}
