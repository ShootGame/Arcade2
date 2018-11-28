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
