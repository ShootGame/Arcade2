package pl.themolka.arcade.util;

import pl.themolka.arcade.time.Time;

public interface Tickable {
    default Time getTickInterval() {
        return Time.TICK;
    }

    void onTick(long tick);
}
