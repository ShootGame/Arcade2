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

public class FinitePercentage extends Percentage {
    protected FinitePercentage(double value) {
        super(value);
    }

    @Override
    public boolean isNormalized() {
        return true;
    }

    @Override
    public FinitePercentage trim() {
        return this;
    }

    protected static FinitePercentage create(double value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("value is smaller than " + MIN_VALUE);
        } else if (value > MAX_VALUE) {
            throw new IllegalArgumentException("value is greater than " + MAX_VALUE);
        }

        return new FinitePercentage(value);
    }
}
