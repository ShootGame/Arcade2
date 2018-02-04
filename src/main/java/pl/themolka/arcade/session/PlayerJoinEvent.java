package pl.themolka.arcade.session;

import pl.themolka.arcade.ArcadePlugin;

public class PlayerJoinEvent extends PlayerEvent {
    private final boolean restored;

    public PlayerJoinEvent(ArcadePlugin plugin, ArcadePlayer player, boolean restored) {
        super(plugin, player);

        this.restored = restored;
    }

    public boolean isRestored() {
        return this.restored;
    }
}
