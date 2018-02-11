package pl.themolka.arcade.map;

import pl.themolka.arcade.time.Time;

public class MapTime implements Cloneable {
    public static final MapTime MIN = ofTicks(0);
    public static final MapTime MAX = ofTicks(24000);

    private final int ticks;
    private boolean locked;

    private MapTime(int ticks) {
        if (ticks < MIN.getTicks() || ticks > MAX.getTicks()) {
            throw new IllegalArgumentException("Illegal time ticks.");
        }

        this.ticks = ticks;
    }

    @Override
    public MapTime clone() {
        try {
            MapTime mapTime = new MapTime(this.ticks);
            mapTime.locked = this.locked;
            return mapTime;
        } finally {
            try {
                super.clone();
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getTicks() {
        return this.ticks;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Time toTime() {
        return Time.ofTicks(this.getTicks());
    }

    //
    // Instancing
    //

    public static MapTime ofTicks(int ticks) {
        return new MapTime(ticks);
    }

    public static MapTime defaultTime() {
        return MapTimeConstants.DAY.time().clone();
    }
}
