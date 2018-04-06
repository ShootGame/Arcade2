package pl.themolka.arcade.util;

public final class Nulls {
    private Nulls() {
    }

    public static <T> T defaults(T nullable, T def) {
        return nullable != null ? nullable : def;
    }
}
