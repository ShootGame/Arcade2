package pl.themolka.arcade.session;

import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;

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
