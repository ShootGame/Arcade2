package pl.themolka.arcade.session;

import org.bukkit.ChatColor;
import pl.themolka.commons.session.Session;

import java.util.UUID;

public class ArcadeSession implements Session<ArcadePlayer> {
    private final ArcadePlayer player;

    public ArcadeSession(ArcadePlayer player) {
        this.player = player;
    }

    @Override
    public ArcadePlayer getRepresenter() {
        return this.player;
    }

    @Override
    public int getSessionId() {
        return 0;
    }

    @Override
    public String getUsername() {
        return this.getRepresenter().getUsername();
    }

    @Override
    public UUID getUuid() {
        return this.getRepresenter().getUuid();
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.getRepresenter().getBukkit().hasPermission(permission);
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public void send(String message) {
        this.getRepresenter().send(message);
    }

    @Override
    public void sendError(String error) {
        this.getRepresenter().sendError(error);
    }

    @Override
    public void sendInfo(String info) {
        this.getRepresenter().sendInfo(info);
    }

    @Override
    public void sendSuccess(String success) {
        this.getRepresenter().sendSuccess(success);
    }

    public void sendTitleMessage(String title) {
        String line = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + " --------------- ";
        this.send(line + ChatColor.RESET + ChatColor.GOLD + title + line);
    }

    public void sendTitleMessage(String title, String extra) {
        String line = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + " --------------- ";
        this.send(line + ChatColor.RESET + ChatColor.GOLD + title + ChatColor.GRAY + " (" + extra + ")" + line);
    }
}
