package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;

/**
 * Some commands are not supported and should never be used.
 */
@ServiceId("BanCommands")
public class BanCommandsService extends Service {
    public static final String COMMAND_MESSAGE = ChatColor.RED +
            "You may not execute %s command on this server.";
    public static final String[] DISABLED_COMMANDS = {"reload", "stop"};

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String prefix = BukkitCommands.BUKKIT_COMMAND_PREFIX.toLowerCase();
        String message = event.getMessage();
        if (!message.startsWith(prefix)) {
            return;
        }

        String query = message.substring(1);
        if (query.isEmpty()) {
            return;
        }

        String label = query.split(" ", 2)[0].toLowerCase();
        if (label.isEmpty()) {
            return;
        }

        Command command = this.getPlugin().getServer().getCommandMap().getCommand(label);
        if (command == null) {
            return;
        }

        for (String disabled : DISABLED_COMMANDS) {
            for (String alias : command.getAliases()) {
                if (command.getName().equalsIgnoreCase(disabled) || alias.equalsIgnoreCase(disabled)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(String.format(COMMAND_MESSAGE, prefix + command.getName()));
                    return;
                }
            }
        }
    }
}
