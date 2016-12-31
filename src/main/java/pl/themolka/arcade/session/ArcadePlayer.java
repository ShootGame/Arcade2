package pl.themolka.arcade.session;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.metadata.Metadata;
import pl.themolka.arcade.metadata.MetadataContainer;
import pl.themolka.arcade.module.Module;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class ArcadePlayer implements Metadata {
    public static final long SOUND_INTERVAL = 500L; // half second

    private final transient Player bukkit;
    private transient GamePlayer gamePlayer;
    private Instant lastPlayedSound;
    private final MetadataContainer metadata = new MetadataContainer();

    public ArcadePlayer(Player bukkit) {
        this.bukkit = bukkit;
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

    public Player getBukkit() {
        return this.bukkit;
    }

    public GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public Instant getLastPlayedSound() {
        return this.lastPlayedSound;
    }

    public String getUsername() {
        return this.getBukkit().getName();
    }

    public UUID getUuid() {
        return this.getBukkit().getUniqueId();
    }

    public void play(ArcadeSound sound) {
        this.play(sound.getSound());
    }

    public void play(Sound sound) {
        this.play(sound, this.getBukkit().getLocation());
    }

    public void play(ArcadeSound sound, Location position) {
        this.play(sound.getSound(), position);
    }

    public void play(Sound sound, Location position) {
        this.play(sound, position, ArcadeSound.DEFAULT_VOLUME);
    }

    public void play(ArcadeSound sound, Location position, float volume) {
        this.play(sound, position, volume);
    }

    public void play(Sound sound, Location position, float volume) {
        this.play(sound, position, volume, ArcadeSound.DEFAULT_PITCH);
    }

    public void play(ArcadeSound sound, Location position, float volume, float pitch) {
        this.play(sound.getSound(), position, volume, pitch);
    }

    public void play(Sound sound, Location position, float volume, float pitch) {
        Instant now = Instant.now();
        if (now.toEpochMilli() - this.getLastPlayedSound().toEpochMilli() < SOUND_INTERVAL) {
            return;
        }

        this.getBukkit().playSound(position, sound, volume, pitch);
        this.lastPlayedSound = now;
    }

    public void send(String message) {
        this.getBukkit().sendMessage(message);
    }

    public void sendError(String error) {
        this.send(ChatColor.RED + error);
    }

    public void sendInfo(String info) {
        this.send(ChatColor.GRAY + info);
    }

    public void sendSuccess(String success) {
        this.send(ChatColor.GREEN + success);
    }

    public void sendTip(String tip) {
        this.send(ChatColor.GRAY + ChatColor.BOLD.toString() + "[Tip] " + ChatColor.RESET + ChatColor.GRAY + ChatColor.ITALIC + tip);
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
