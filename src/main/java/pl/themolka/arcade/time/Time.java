/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class Time {
    //
    // Special Values
    //

    public static final Time FOREVER = ConstantFactory.createForever();
    public static final Time ZERO = ConstantFactory.createZero();

    //
    // Single Values
    //

    public static final Time MILLISECOND = Time.ofMillis(1);
    public static final Time TICK = Time.ofTicks(1);
    public static final Time SECOND = Time.ofSeconds(1);
    public static final Time MINUTE = Time.ofMinutes(1);
    public static final Time HOUR = Time.ofHours(1);
    public static final Time DAY = Time.ofDays(1);
    public static final Time WEEK = Time.ofWeeks(1);
    public static final Time MONTH = Time.ofMonths(1);
    public static final Time YEAR = Time.ofYears(1);

    //
    // Units
    //

    public static final String FOREVER_KEY = "oo";
    public static final char UNIT_MILLIS = 'x';
    public static final char UNIT_TICKS = 't';
    public static final char UNIT_SECONDS = 's';
    public static final char UNIT_MINUTES = 'm';
    public static final char UNIT_HOURS = 'h';
    public static final char UNIT_DAYS = 'd';
    public static final char UNIT_WEEKS = 'w';
    public static final char UNIT_MONTHS = 'o';
    public static final char UNIT_YEARS = 'y';

    //
    // Factories
    //

    private static Time create(long time) {
        return new Time(time);
    }

    private static class ConstantFactory {
        static Time createForever() {
            return create(Long.MAX_VALUE);
        }

        static Time createZero() {
            return create(0L);
        }
    }

    //
    // Time
    //

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

    @Override
    public String toString() {
        return this.isForever() ? FOREVER_KEY : Long.toString(this.getTime());
    }

    //
    // Queries
    //

    /**
     * Is THIS after the given target?
     */
    public boolean isAfter(Time target) {
        return this.getTime() > target.getTime();
    }
    /**
     * Is THIS before the given target?
     */
    public boolean isBefore(Time target) {
        return this.getTime() < target.getTime();
    }

    public boolean isForever() {
        return FOREVER.equals(this);
    }

    public boolean isNegative() {
        return this.getTime() < ZERO.getTime();
    }

    public boolean isPositive() {
        return this.getTime() > ZERO.getTime();
    }

    public boolean isZero() {
        return ZERO.equals(this);
    }

    //
    // Subtraction
    //

    public Time minus(Time time) {
        return minusMillis(time.toMillis());
    }

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

    public Time minusWeeks(long weeks) {
        return Time.ofWeeks(this.toWeeks() - weeks);
    }

    public Time minusMonths(long months) {
        return Time.ofMonths(this.toMonths() - months);
    }

    public Time minusYears(long years) {
        return Time.ofYears(this.toYears() - years);
    }

    //
    // Addition
    //

    public Time plus(Time time) {
        return plusMillis(time.toMillis());
    }

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

    public Time plusWeeks(long weeks) {
        return Time.ofWeeks(this.toWeeks() + weeks);
    }

    public Time plusMonths(long months) {
        return Time.ofMonths(this.toMonths() + months);
    }

    public Time plusYears(long years) {
        return Time.ofYears(this.toYears() + years);
    }

    //
    // Converting
    //

    public Date toDate() {
        return Date.from(this.toInstant());
    }

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

    public long toWeeks() {
        return this.toDays() / 7L;
    }

    public long toMonths() {
        return this.toDays() / 31L;
    }

    public long toYears() {
        return this.toDays() / 365L;
    }

    //
    // Instancing
    //

    public static Time clone(Time time) {
        return ofMillis(time.toMillis());
    }

    public static Time now() {
        return ofMillis(System.currentTimeMillis());
    }

    public static Time of(Date date) {
        return of(date.toInstant());
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
            case UNIT_WEEKS: return ofWeeks(time);
            case UNIT_MONTHS: return ofMonths(time);
            case UNIT_YEARS: return ofYears(time);
            default: return def;
        }
    }

    public static Time ofMillis(long millis) {
        return create(millis);
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

    public static Time ofWeeks(long weeks) {
        return ofDays(weeks * 7L);
    }

    public static Time ofMonths(long months) {
        return ofDays(months * 31L);
    }

    public static Time ofYears(long years) {
        return ofDays(years * 365L);
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

        if (time == null) {
            return def;
        } else if (time.equalsIgnoreCase(foreverKey)) {
            return FOREVER;
        }

        char unit = time.toLowerCase().charAt(time.length() - 1);

        try {
            return of(unit, Long.parseLong(time.substring(0, time.length() - 1)), def);
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public static Time valueOf(String string) {
        return parseTime(string);
    }
}
