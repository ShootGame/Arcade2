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
