package pl.themolka.arcade.session;

import pl.themolka.arcade.ArcadePlugin;

public class ArcadePlayerQuitEvent extends ArcadePlayerEvent {
    public ArcadePlayerQuitEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin, player);
    }
}
