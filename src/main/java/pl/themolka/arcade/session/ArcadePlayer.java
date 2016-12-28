package pl.themolka.arcade.session;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.Metadatable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ArcadePlayer implements Metadatable {
    public static final long SOUND_INTERVAL = 500L; // half second

    private final Player bukkit;
    private GamePlayer gamePlayer;
    private Instant lastPlayedSound;
    private final Map<String, Object> metadata = new HashMap<>();

    public ArcadePlayer(Player bukkit) {
        this.bukkit = bukkit;
    }

    @Override
    public Object getMetadata(String key, Object def) {
        return this.metadata.getOrDefault(key, def);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return this.metadata.keySet();
    }

    @Override
    public void setMetadata(String key, Object metadata) {
        this.metadata.put(key, metadata);
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

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
