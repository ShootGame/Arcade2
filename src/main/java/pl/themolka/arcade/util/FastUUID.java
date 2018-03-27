package pl.themolka.arcade.util;

import java.util.Random;
import java.util.UUID;

public class FastUUID {
    private final Random random = new Random();

    public UUID next() {
        return new UUID(this.random.nextLong(), this.random.nextLong());
    }
}
