package pl.themolka.arcade.permission;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.HashMap;
import java.util.UUID;

public class ClientPermissionStorage extends HashMap<UUID, Group[]> {
    private final Group defaultGroup;

    public ClientPermissionStorage(Group defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public Group[] fetch(UUID id) {
        Group[] def = new Group[] {this.getDefaultGroup()};
        return this.getOrDefault(id, def);
    }

    public Group[] fetch(ArcadePlayer player) {
        return this.fetch(player.getUuid());
    }

    public Group[] fetch(GamePlayer player) {
        return this.fetch(player.getUuid());
    }

    public Group getDefaultGroup() {
        return this.defaultGroup;
    }
}
