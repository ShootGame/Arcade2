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

package pl.themolka.arcade.parser.number;

import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Integer.class)
public class IntegerParser extends NumberParser<Integer> {
    public IntegerParser() {
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a real number (integer)");
    }

    @Override
    protected Integer parse(String input) throws NumberFormatException {
        return Integer.parseInt(input);
    }

    @Override
    protected Integer positiveInfinity() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected Integer negativeInfinity() {
        return Integer.MIN_VALUE;
    }
}
