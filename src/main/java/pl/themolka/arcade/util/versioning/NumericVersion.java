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

package pl.themolka.arcade.util.versioning;

import org.apache.commons.lang3.Validate;

import java.util.Objects;

public class NumericVersion extends Version.Impl<NumericVersion> {
    public static final NumericVersion DEFAULT = new NumericVersion(0);

    private final int value;

    public NumericVersion(int value) {
        Validate.isTrue(value >= 0, "value cannot be negative");
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericVersion that = (NumericVersion) o;
        return value == that.value;
    }

    @Override
    public boolean isOldenThan(NumericVersion than) {
        return this.value < Objects.requireNonNull(than, "than cannot be null").value;
    }

    @Override
    public boolean isNewerThan(NumericVersion than) {
        return this.value > Objects.requireNonNull(than, "than cannot be null").value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public NumericVersion previous() throws NoPreviousException {
        if (this.value <= 0) {
            throw new NoPreviousException();
        }

        return new NumericVersion(this.value - 1);
    }

    @Override
    public NumericVersion next() throws NoNextException {
        return new NumericVersion(this.value + 1);
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
