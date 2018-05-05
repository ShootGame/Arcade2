package pl.themolka.arcade.util;

import java.util.Objects;

public class Percentage {
    public static final double MIN_VALUE = 0D;
    public static final double MAX_VALUE = 1D;

    public static final FinitePercentage ZERO = finite(MIN_VALUE);
    public static final FinitePercentage DONE = finite(MAX_VALUE);

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

    public double calculate(double input) {
        return input * this.value;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    public boolean isDone() {
        return this.value == MAX_VALUE;
    }

    public boolean isNormalized() {
        return this.value >= MIN_VALUE && this.value <= MAX_VALUE;
    }

    public boolean isZero() {
        return this.value == MIN_VALUE;
    }

    @Override
    public String toString() {
        return Double.toString(this.value * 100D) + SYMBOL;
    }

    public Percentage trim() {
        if (this.value < MIN_VALUE) {
            return new Percentage(MIN_VALUE);
        } else if (this.value > MAX_VALUE) {
            return new Percentage(MAX_VALUE);
        }

        return this;
    }

    //
    // Instancing
    //

    public static FinitePercentage finite(double value) {
        return FinitePercentage.create(value);
    }

    public static Percentage infinite(double value) {
        return new Percentage(value);
    }

    public static Percentage random() {
        return finite(Math.random());
    }

    public static FinitePercentage trim(double value) {
        return finite(infinite(value).trim().value);
    }
}
