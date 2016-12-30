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
        return createLine(length, "-");
    }

    public static String createLine(int length, String type) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append(type);
        }

        return line.toString();
    }

    public static void sendTitleMessage(Session<?> session, String title) {
        sendTitleMessage(session, title, null);
    }

    public static void sendTitleMessage(Session<?> session, String title, String extra) {
        String head = ChatColor.GOLD + title;
        if (extra != null) {
            head += ChatColor.GRAY + " (" + extra + ")";
        }

        int length = (CHAT_LINE_LENGTH - head.length()) / 2;
        String line = " " + ChatColor.GRAY + ChatColor.STRIKETHROUGH + createLine(length) + ChatColor.RESET + " ";

        session.send(line + head + line);
    }
}
