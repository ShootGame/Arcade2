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

import java.util.Objects;
import java.util.Random;

public abstract class RandomSource<T> {
    protected final Random random;

    public RandomSource() {
        this(new Random());
    }

    public RandomSource(long seed) {
        this(new Random(seed));
    }

    public RandomSource(Random random) {
        this.random = Objects.requireNonNull(random, "random cannot be null");
    }

    public abstract T random();
}
