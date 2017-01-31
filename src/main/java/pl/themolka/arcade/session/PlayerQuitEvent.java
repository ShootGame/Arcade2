package pl.themolka.arcade.session;

import pl.themolka.arcade.ArcadePlugin;

public class PlayerQuitEvent extends ArcadePlayerEvent {
    public PlayerQuitEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin, player);
    }
}
