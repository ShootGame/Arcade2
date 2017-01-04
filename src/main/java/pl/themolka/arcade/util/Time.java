package pl.themolka.arcade.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Time {
    public static final Time FOREVER = new Time(Long.MAX_VALUE);
    public static final String FOREVER_KEY = "oo";

    public static final char UNIT_MILLIS = 'x';
    public static final char UNIT_TICKS = 't';
    public static final char UNIT_SECONDS = 's';
    public static final char UNIT_MINUTES = 'm';
    public static final char UNIT_HOURS = 'h';
    public static final char UNIT_DAYS = 'd';

    private final long time;

    private Time(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && this.getTime() == ((Time) obj).getTime();
    }

    public long getTime() {
        return this.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.time);
    }

    public boolean isForever() {
        return this.equals(FOREVER);
    }

    //
    // Subtraction
    //

    public Time minusMillis(long millis) {
        return Time.ofMillis(this.toMillis() - millis);
    }

    public Time minusTicks(long ticks) {
        return Time.ofTicks(this.toTicks() - ticks);
    }

    public Time minusSeconds(long seconds) {
        return Time.ofSeconds(this.toSeconds() - seconds);
    }

    public Time minusMinutes(long minutes) {
        return Time.ofMinutes(this.toMinutes() - minutes);
    }

    public Time minusHours(long hours) {
        return Time.ofHours(this.toHours() - hours);
    }

    public Time minusDays(long days) {
        return Time.ofDays(this.toDays() - days);
    }

    //
    // Addition
    //

    public Time plusMillis(long millis) {
        return Time.ofMillis(this.toMillis() + millis);
    }

    public Time plusTicks(long ticks) {
        return Time.ofTicks(this.toTicks() + ticks);
    }

    public Time plusSeconds(long seconds) {
        return Time.ofSeconds(this.toSeconds() + seconds);
    }

    public Time plusMinutes(long minutes) {
        return Time.ofMinutes(this.toMinutes() + minutes);
    }

    public Time plusHours(long hours) {
        return Time.ofHours(this.toHours() + hours);
    }

    public Time plusDays(long days) {
        return Time.ofDays(this.toDays() + days);
    }

    //
    // Converting
    //

    public Duration toDuration() {
        return Duration.ofMillis(this.toMillis());
    }

    public Instant toInstant() {
        return Instant.ofEpochMilli(this.toMillis());
    }

    public long toMillis() {
        return this.getTime();
    }

    public long toTicks() {
        return this.toMillis() / 50L;
    }

    public long toSeconds() {
        return this.toTicks() / 20L;
    }

    public long toMinutes() {
        return this.toSeconds() / 60L;
    }

    public long toHours() {
        return this.toMinutes() / 60L;
    }

    public long toDays() {
        return this.toHours() / 24L;
    }

    //
    // Instancing
    //

    public static Time now() {
        return ofMillis(System.currentTimeMillis());
    }

    public static Time of(Duration duration) {
        return ofMillis(duration.toMillis());
    }

    public static Time of(Instant instant) {
        return ofMillis(instant.toEpochMilli());
    }

    public static Time of(char unit, long time) {
        return of(unit, time, null);
    }

    public static Time of(char unit, long time, Time def) {
        switch (unit) {
            case UNIT_MILLIS: return ofMillis(time);
            case UNIT_TICKS: return ofTicks(time);
            case UNIT_SECONDS: return ofSeconds(time);
            case UNIT_MINUTES: return ofMinutes(time);
            case UNIT_HOURS: return ofHours(time);
            case UNIT_DAYS: return ofDays(time);
            default: return def;
        }
    }

    public static Time ofMillis(long millis) {
        return new Time(millis);
    }

    public static Time ofTicks(long ticks) {
        return ofMillis(ticks * 50L);
    }

    public static Time ofSeconds(long seconds) {
        return ofTicks(seconds * 20L);
    }

    public static Time ofMinutes(long minutes) {
        return ofSeconds(minutes * 60L);
    }

    public static Time ofHours(long hours) {
        return ofMinutes(hours * 60L);
    }

    public static Time ofDays(long days) {
        return ofHours(days * 24L);
    }

    //
    // Parsing
    //

    public static Time parseTime(String time) {
        return parseTime(time, null);
    }

    public static Time parseTime(String time, Time def) {
        return parseTime(time, def, null);
    }

    public static Time parseTime(String time, Time def, String foreverKey) {
        if (foreverKey == null) {
            foreverKey = FOREVER_KEY;
        }

        if (time.equalsIgnoreCase(foreverKey)) {
            return FOREVER;
        }

        char unit = time.toLowerCase().charAt(time.length() - 1);

        try {
            return of(unit, Long.parseLong(time.substring(0, time.length() - 1)), def);
        } catch (NumberFormatException ignored) {
        }

        return def;
    }

    public static Time valueOf(String string) {
        return parseTime(string);
    }
}
