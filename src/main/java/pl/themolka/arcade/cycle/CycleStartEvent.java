package pl.themolka.arcade.cycle;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.OfflineMap;

public class CycleStartEvent extends CycleEvent {
    private final int seconds;

    public CycleStartEvent(ArcadePlugin plugin, OfflineMap nextMap, int seconds) {
        super(plugin, nextMap);

        this.seconds = seconds;
    }

    public int getSeconds() {
        return this.seconds;
    }
}
