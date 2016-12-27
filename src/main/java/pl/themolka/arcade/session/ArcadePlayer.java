package pl.themolka.arcade.session;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.themolka.arcade.util.Metadatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ArcadePlayer implements Metadatable {
    private final Player bukkit;
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

    public String getUsername() {
        return this.getBukkit().getName();
    }

    public UUID getUuid() {
        return this.getBukkit().getUniqueId();
    }

    public void reset() {

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
}
