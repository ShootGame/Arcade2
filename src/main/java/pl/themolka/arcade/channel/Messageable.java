package pl.themolka.arcade.channel;

import org.bukkit.ChatColor;

public interface Messageable {
    ChatColor ERROR_COLOR = ChatColor.RED;
    ChatColor INFO_COLOR = ChatColor.GRAY;
    ChatColor SUCCESS_COLOR = ChatColor.GREEN;
    ChatColor TIP_COLOR = ChatColor.AQUA;

    void send(String message);

    void sendAction(String action);

    void sendChat(String chat);

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
