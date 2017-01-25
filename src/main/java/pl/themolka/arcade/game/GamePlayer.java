package pl.themolka.arcade.game;

import org.bukkit.entity.Player;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.Set;
import java.util.UUID;

public class GamePlayer implements Metadata, Sender {
    private ChatChannel channel;
    private String displayName;
    private final transient Game game;
    private final MetadataContainer metadata = new MetadataContainer();
    private boolean participating;
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
    public boolean hasPermission(String permission) {
        return this.getPlayer().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void send(String message) {
        this.getPlayer().send(message);
    }

    @Override
    public void sendAction(String action) {
        this.getPlayer().sendAction(action);
    }

    @Override
    public void sendChat(String chat) {
        this.getPlayer().sendChat(chat);
    }

    @Override
    public void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata) {
        this.metadata.setMetadata(owner, key, metadata);
    }

    public Player getBukkit() {
        if (this.isOnline()) {
            return this.getPlayer().getBukkit();
        }

        return null;
    }

    public ChatChannel getCurrentChannel() {
        return this.channel;
    }

    public String getDisplayName() {
        if (this.displayName != null) {
            return this.displayName;
        }

        return this.getPlayer().getDisplayName();
    }

    public String getFullName() {
        return this.getDisplayName();
    }

    public Game getGame() {
        return this.game;
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

    public boolean hasDisplayName() {
        return this.displayName != null || (this.isOnline() && this.getPlayer().getDisplayName() != null);
    }

    public boolean isOnline() {
        return this.player != null;
    }

    public boolean isParticipating() {
        return this.participating;
    }

    public void resetDisplayName() {
        this.setDisplayName(null);
    }

    public void setCurrentChannel(ChatChannel channel) {
        this.channel = channel;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;

        this.getPlayer().setDisplayName(displayName);
    }

    public void setParticipating(boolean participating) {
        this.participating = participating;
    }

    public void setPlayer(ArcadePlayer player) {
        this.player = player;
    }
}
