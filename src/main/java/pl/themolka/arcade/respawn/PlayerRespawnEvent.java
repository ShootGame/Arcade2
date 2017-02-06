package pl.themolka.arcade.respawn;

import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerEvent;

public class PlayerRespawnEvent extends PlayerEvent {
    private Location respawnPosition;

    public PlayerRespawnEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin, player);
    }

    public Location getRespawnPosition() {
        return respawnPosition;
    }

    public void setRespawnPosition(Location respawnPosition) {
        this.respawnPosition = respawnPosition;
    }
}
