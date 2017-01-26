package pl.themolka.arcade.respawn;

import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadePlayerEvent;

public class ArcadePlayerRespawnEvent extends ArcadePlayerEvent {
    private Location respawnPosition;

    public ArcadePlayerRespawnEvent(ArcadePlugin plugin, ArcadePlayer player) {
        super(plugin, player);
    }

    public Location getRespawnPosition() {
        return respawnPosition;
    }

    public void setRespawnPosition(Location respawnPosition) {
        this.respawnPosition = respawnPosition;
    }
}
