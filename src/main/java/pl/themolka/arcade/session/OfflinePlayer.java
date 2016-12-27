package pl.themolka.arcade.session;

import java.util.UUID;

public class OfflinePlayer {
    private final String username;
    private final UUID uuid;

    public OfflinePlayer(String username) {
        this(username, null);
    }

    public OfflinePlayer(UUID uuid) {
        this(null, uuid);
    }

    public OfflinePlayer(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
