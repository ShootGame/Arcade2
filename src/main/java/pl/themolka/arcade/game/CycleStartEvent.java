package pl.themolka.arcade.game;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.OfflineMap;

public class CycleStartEvent extends CycleEvent {
    public CycleStartEvent(ArcadePlugin plugin, OfflineMap nextMap) {
        super(plugin, nextMap);
    }
}
