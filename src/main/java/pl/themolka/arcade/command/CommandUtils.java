package pl.themolka.arcade.command;

import org.bukkit.ChatColor;

public class CommandUtils {
    public static final int CHAT_LINE_LENGTH = 50;

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
        String head = ChatColor.GOLD + title + ChatColor.RESET;
        if (extra != null) {
            head += ChatColor.GRAY + " (" + extra + ")";
        }

        int length = (CHAT_LINE_LENGTH - head.length()) / 2;
        String line = " " + createLine(length) + ChatColor.RESET + " ";

        return (line + head + line).trim();
    }

    public static void sendTitleMessage(Sender to, String title) {
        sendTitleMessage(to, title, null);
    }

    public static void sendTitleMessage(Sender to, String title, String extra) {
        to.send(createTitle(title, extra));
    }
}
