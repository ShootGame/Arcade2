package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.UUID;

public interface Sender {
    ChatColor ERROR_COLOR = ChatColor.RED;
    ChatColor INFO_COLOR = ChatColor.GRAY;
    ChatColor SUCCESS_COLOR = ChatColor.GREEN;
    ChatColor TIP_COLOR = ChatColor.AQUA;

    default GamePlayer getGamePlayer() {
        return this.getPlayer().getGamePlayer();
    }

    ArcadePlayer getPlayer();

    String getUsername();

    UUID getUuid();

    boolean hasPermission(String permission);

    boolean isConsole();

    void send(String message);

    default void sendError(String error) {
        this.send(ERROR_COLOR + error);
    }

    default void sendInfo(String info) {
        this.send(INFO_COLOR + info);
    }

    default void sendSuccess(String success) {
        this.send(SUCCESS_COLOR + success);
    }

    default void sendTip(String tip) {
        this.send(TIP_COLOR + ChatColor.BOLD.toString() + "[Tip] " + ChatColor.RESET + INFO_COLOR + ChatColor.ITALIC + tip);
    }
}
