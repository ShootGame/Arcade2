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
