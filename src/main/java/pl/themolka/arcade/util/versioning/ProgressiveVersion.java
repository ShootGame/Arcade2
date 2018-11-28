package pl.themolka.arcade.util.versioning;

import org.apache.commons.lang3.Validate;

import java.util.Objects;

public class ProgressiveVersion extends Version.Impl<ProgressiveVersion> {
    public static final ProgressiveVersion DEFAULT = new ProgressiveVersion(0);

    private final int value;

    public ProgressiveVersion(int value) {
        Validate.isTrue(value >= 0, "value cannot be negative");
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgressiveVersion that = (ProgressiveVersion) o;
        return value == that.value;
    }

    @Override
    public boolean isOldenThan(ProgressiveVersion than) {
        return this.value < Objects.requireNonNull(than, "than cannot be null").value;
    }

    @Override
    public boolean isNewerThan(ProgressiveVersion than) {
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
    public ProgressiveVersion previous() throws NoPreviousException {
        if (this.value == 0) {
            throw new NoPreviousException();
        }

        return new ProgressiveVersion(this.value - 1);
    }

    @Override
    public ProgressiveVersion next() {
        return new ProgressiveVersion(this.value + 1);
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
