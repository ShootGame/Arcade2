package pl.themolka.arcade.session;

import pl.themolka.arcade.ArcadePlugin;

public class PlayerJoinEvent extends ArcadePlayerEvent {
    public PlayerJoinEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin, player);
    }
}
