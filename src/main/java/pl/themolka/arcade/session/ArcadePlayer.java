package pl.themolka.arcade.session;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArcadePlayer {
    private final Player bukkit;

    public ArcadePlayer(Player bukkit) {
        this.bukkit = bukkit;
    }

    public Player getBukkit() {
        return this.bukkit;
    }

    public String getUsername() {
        return this.getBukkit().getName();
    }

    public UUID getUuuid() {
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
