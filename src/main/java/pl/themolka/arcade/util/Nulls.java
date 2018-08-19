package pl.themolka.arcade.util;

public final class Nulls {
    private Nulls() {
    }

    public static <T> T defaults(T nullable, T def) {
        return nullable != null ? nullable : def;
    }

    public static <T> T or(T... or) {
        for (T value : or) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }
}
