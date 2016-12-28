package pl.themolka.arcade.game;

import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.util.Metadatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GamePlayer implements Metadatable {
    public static final String METADATA_KEY_SEPARATOR = "#";

    private final Game game;
    private final Map<String, Object> metadata = new HashMap<>();
    private ArcadePlayer player;
    private final String username;
    private final UUID uuid;

    public GamePlayer(Game game, ArcadePlayer player) {
        this(game, player.getUsername(), player.getUuid());

        this.setPlayer(player);
    }

    public GamePlayer(Game game, String username, UUID uuid) {
        this.game = game;
        this.username = username;
        this.uuid = uuid;
    }

    /**
     * Should not be used.
     * Use {@link #getMetadata(Class, String)} or {@link #getMetadata(Class, String, Object)} instead
     */
    @Deprecated
    @Override
    public Object getMetadata(String key, Object def) {
        return this.metadata.getOrDefault(key, def);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return this.metadata.keySet();
    }

    /**
     * Should not be used.
     * Use {@link #setMetadata(Class, String, Object)} instead
     */
    @Deprecated
    @Override
    public void setMetadata(String key, Object def) {
        this.metadata.put(key, def);
    }

    public Game getGame() {
        return this.game;
    }

    public Object getMetadata(Class<? extends Module<?>> owner, String key) {
        return this.getMetadata(owner, key, null);
    }

    public Object getMetadata(Class<? extends Module<?>> owner, String key, Object def) {
        return this.getMetadata(owner.getName() + METADATA_KEY_SEPARATOR + key, def);
    }

    public boolean hasMetadata(Class<? extends Module<?>> owner, String key) {
        return this.getMetadata(owner, key) != null;
    }

    public boolean isOnline() {
        return this.player != null;
    }

    public void setMetadata(Class<? extends Module<?>> owner, String key, Object def) {
        this.setMetadata(owner.getName() + METADATA_KEY_SEPARATOR + key, def);
    }

    public ArcadePlayer getPlayer() {
        return this.player;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void reset() {

    }

    public void setPlayer(ArcadePlayer player) {
        this.player = player;
    }
}
