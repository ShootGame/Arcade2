package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.commons.command.BukkitCommands;
import pl.themolka.commons.session.Session;

public class Commands extends BukkitCommands {
    public Commands(ArcadePlugin plugin) {
        super(plugin);
    }

    public static void sendTitleMessage(Session<?> session, String title) {
        String line = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + " --------------- ";
        session.send(line + ChatColor.RESET + ChatColor.GOLD + title + line);
    }

    public static void sendTitleMessage(Session<?> session, String title, String extra) {
        String line = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + " --------------- ";
        session.send(line + ChatColor.RESET + ChatColor.GOLD + title + ChatColor.GRAY + " (" + extra + ")" + line);
    }
}
