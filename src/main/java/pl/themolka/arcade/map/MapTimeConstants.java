package pl.themolka.arcade.map;

public enum MapTimeConstants {
    DAY(6000),
    NIGHT(12000),
    ;

    private final int ticks;

    MapTimeConstants(int ticks) {
        this.ticks = ticks;
    }

    public MapTime time() {
        return MapTime.ofTicks(this.ticks);
    }
}
