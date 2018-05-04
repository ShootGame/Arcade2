package pl.themolka.arcade.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.UUID;

public interface PlayerResolver {
    PlayerResolver NULL = new NullPlayerResolver();

    default GamePlayer resolve(ArcadePlayer player) {
        return player.getGamePlayer();
    }

    default GamePlayer resolve(CommandSender sender) {
        return sender instanceof Player ? this.resolve((Player) sender) : null;
    }

    default GamePlayer resolve(Entity entity) {
        return entity instanceof Player ? this.resolve((Player) entity) : null;
    }

    default GamePlayer resolve(GamePlayerSnapshot snapshot) {
        return this.resolve(snapshot.getUuid());
    }

    default GamePlayer resolve(Player bukkit) {
        return this.resolve(bukkit.getUniqueId());
    }

    GamePlayer resolve(String username);

    GamePlayer resolve(UUID uniqueId);
}

class NullPlayerResolver implements PlayerResolver {
    @Override
    public GamePlayer resolve(String username) {
        return null;
    }

    @Override
    public GamePlayer resolve(UUID uniqueId) {
        return null;
    }
}
