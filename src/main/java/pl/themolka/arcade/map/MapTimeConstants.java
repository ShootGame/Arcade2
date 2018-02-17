package pl.themolka.arcade.map;

/**
 * See https://minecraft.gamepedia.com/Day-night_cycle
 */
public enum MapTimeConstants {
    // day
    SUNRISE(0L),
    MORNING(1000L),
    FORENOON(4000L),
    NOON(6000L), MIDDAY(NOON.ticks()),
    AFTERNOON(8000L),
    EVENING(11000L),
    SUNSET(13000L),

    // night
    MUSK(13800L), // mobs may spawn here

    MOONRISE(14500L),
    MIDNIGHT(18000L),
    MOONSET(21500L),

    DAWN(23000L), // mobs may spawn here

    // extra shortcuts
    DAY(MIDDAY.ticks()),
    NIGHT(MIDNIGHT.ticks()),
    ;

    private final MapTime time;

    MapTimeConstants(long ticks) {
        this.time = MapTime.ofTicks(ticks);
    }

    public long ticks() {
        return this.time.getTicks();
    }

    public MapTime time() {
        return this.time;
    }
}
