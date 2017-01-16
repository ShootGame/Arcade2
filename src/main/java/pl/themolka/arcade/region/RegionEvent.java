package pl.themolka.arcade.region;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class RegionEvent extends GameEvent {
    private final Region region;
    
    public RegionEvent(ArcadePlugin plugin, Region region) {
        super(plugin);

        this.region = region;
    }

    public Region getRegion() {
        return this.region;
    }
}
