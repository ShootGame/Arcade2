package pl.themolka.arcade.game;

import org.bukkit.GameMode;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.Set;
import java.util.UUID;

public class GamePlayer implements Metadata {
    private final transient Game game;
    private final MetadataContainer metadata = new MetadataContainer();
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

    @Override
    public Object getMetadata(Class<? extends Module<?>> owner, String key, Object def) {
        return this.metadata.getMetadata(owner, key, def);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return this.metadata.getMetadataKeys();
    }

    @Override
    public void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata) {
        this.metadata.setMetadata(owner, key, metadata);
    }

    public Game getGame() {
        return this.game;
    }

    public boolean isOnline() {
        return this.player != null;
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
        this.getPlayer().getBukkit().setGameMode(GameMode.CREATIVE);
    }

    public void setPlayer(ArcadePlayer player) {
        this.player = player;
    }
}
