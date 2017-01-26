package pl.themolka.arcade.permission;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.HashMap;
import java.util.UUID;

public class ClientPermissionStorage extends HashMap<UUID, Group[]> {
    public Group[] fetch(UUID id) {
        return this.get(id);
    }

    public Group[] fetch(ArcadePlayer player) {
        return this.fetch(player.getUuid());
    }

    public Group[] fetch(GamePlayer player) {
        return this.fetch(player.getUuid());
    }
}
