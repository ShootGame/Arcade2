package pl.themolka.arcade.session;

import pl.themolka.arcade.ArcadePlugin;

public class ArcadePlayerJoinEvent extends ArcadePlayerEvent {
    public ArcadePlayerJoinEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin, player);
    }
}
