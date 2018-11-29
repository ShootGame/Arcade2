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

import java.util.Objects;

public interface Version<T extends Version<T>> extends Comparable<T> {
    @Override
    default int compareTo(T version) {
        return compare(this, version);
    }

    boolean isOldenThan(T than);

    boolean isEqualTo(T to);

    boolean isNewerThan(T than);

    T previous() throws NoPreviousException;

    T next() throws NoNextException;

    static <T extends Version<T>> int compare(Version<T> a, Version<T> b) {
        Objects.requireNonNull(a, "a cannot be null");
        Objects.requireNonNull(b, "b cannot be null");

        if (a.isOldenThan((T) b)) {
            return -1;
        } else if (a.isNewerThan((T) b)) {
            return 1;
        } else {
            return 0;
        }
    }

    abstract class Impl<T extends Version<T>> implements Version<T> {
        @Override
        public boolean isEqualTo(T to) {
            return this.equals(to);
        }

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public abstract int hashCode();

        @Override
        public abstract String toString();
    }

    class NoPreviousException extends RuntimeException {
    }

    class NoNextException extends RuntimeException {
    }
}
