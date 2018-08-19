package pl.themolka.arcade.util;

import java.util.Random;
import java.util.UUID;

public class FastUUID extends RandomSource<UUID> {
    public FastUUID() {
    }

    public FastUUID(long seed) {
        super(seed);
    }

    public FastUUID(Random random) {
        super(random);
    }

    @Override
    public UUID random() {
        return new UUID(this.random.nextLong(), this.random.nextLong());
    }
}
