package pl.themolka.arcade.game;

import pl.themolka.arcade.util.Snapshot;

import java.util.UUID;

public class GamePlayerSnapshot implements Snapshot<GamePlayer> {
    private final String displayName;
    private final boolean participating;
    private final String username;
    private final UUID uuid;

    public GamePlayerSnapshot(GamePlayer source) {
        this(source.getDisplayName(),
                source.isParticipating(),
                source.getUsername(),
                source.getUuid());
    }

    public GamePlayerSnapshot(String displayName,
                              boolean participating,
                              String username,
                              UUID uuid) {
        this.displayName = displayName;
        this.participating = participating;
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    public Class<GamePlayer> getSnapshotType() {
        return GamePlayer.class;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isParticipating() {
        return this.participating;
    }
}
