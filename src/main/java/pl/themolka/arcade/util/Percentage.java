package pl.themolka.arcade.util;

import java.util.Objects;

public class Percentage {
    public static final double MIN_VALUE = 0D;
    public static final double MAX_VALUE = 1D;

    public static final char SYMBOL = '%';

    private final double value;

    protected Percentage(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Percentage) {
            Percentage that = (Percentage) obj;
            return Objects.equals(this.value, that.value);
        }

        return false;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    public boolean isNormalized() {
        return this.value >= MIN_VALUE && this.value <= MAX_VALUE;
    }

    @Override
    public String toString() {
        return Double.toString(this.value * 100D) + SYMBOL;
    }

    //
    // Instancing
    //

    public static Percentage finite(double value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("value is smaller than " + MIN_VALUE);
        } else if (value > MAX_VALUE) {
            throw new IllegalArgumentException("value is greater than " + MAX_VALUE);
        }

        return infinite(value);
    }

    public static Percentage infinite(double value) {
        return new Percentage(value);
    }
}
