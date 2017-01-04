package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.command.BukkitCommands;
import pl.themolka.commons.session.Session;

public class Commands extends BukkitCommands {
    public static final int CHAT_LINE_LENGTH = 50;

    public Commands(ArcadePlugin plugin) {
        super(plugin);
    }

    public static String createLine(int length) {
        return createLine(length, ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-");
    }

    public static String createLine(int length, String type) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append(type);
        }

        return line.toString();
    }

    public static String createTitle(String title) {
        return createTitle(title, null);
    }

    public static String createTitle(String title, String extra) {
        String head = ChatColor.GOLD + title;
        if (extra != null) {
            head += ChatColor.GRAY + " (" + extra + ")";
        }

        int length = (CHAT_LINE_LENGTH - head.length()) / 2;
        String line = " " + createLine(length) + ChatColor.RESET + " ";

        return line + head + line;
    }

    public static void sendTitleMessage(Session<?> session, String title) {
        sendTitleMessage(session, title, null);
    }

    public static void sendTitleMessage(Session<?> session, String title, String extra) {
        session.send(createTitle(title, extra));
    }
}
