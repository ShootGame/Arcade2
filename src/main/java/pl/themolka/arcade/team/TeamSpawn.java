package pl.themolka.arcade.team;

import org.bukkit.Location;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public interface TeamSpawn extends PlayerApplicable {
    @Override
    default void apply(GamePlayer player) {
        Location spawn;
        do {
            spawn = this.getSpawnLocation();
        } while (spawn == null);

        player.getBukkit().teleport(spawn);
    }

    Location getSpawnLocation();
}
