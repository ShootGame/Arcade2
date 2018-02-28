package pl.themolka.arcade.map;

import java.util.Objects;

public class RandomSeed {
    public static final long DEFAULT_SEED = 0L;

    private final long seed;

    public RandomSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RandomSeed) {
            RandomSeed that = (RandomSeed) obj;
            return that.seed == this.seed;
        }

        return false;
    }

    public long getSeed() {
        return this.seed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.seed);
    }

    @Override
    public String toString() {
        return Long.toString(this.seed);
    }
}
