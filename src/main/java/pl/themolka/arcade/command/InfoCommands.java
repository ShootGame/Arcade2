package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.Task;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.time.Time;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.session.Session;

public class InfoCommands {
    private final ArcadePlugin plugin;

    private TPSHandler tps;

    public InfoCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @CommandInfo(name = "info",
            description = "Describe server information",
            permission = "arcade.command.info")
    public void info(Session<ArcadePlayer> sender, CommandContext context) {
        String command = context.getParam(0);

        if (command != null) {
            switch (command.toLowerCase()) {
                case "tps":
                case "lag":
                case "gc":
                    this.printTps(sender);
            }
        } else {
            this.printHelp(sender);
        }
    }

    public void printHelp(Session<ArcadePlayer> sender) {
        Commands.sendTitleMessage(sender, "Help");
        sender.send(ChatColor.GOLD + "/info tps" + ChatColor.GRAY + " - lost ticks per second");
    }

    private void printTps(Session<ArcadePlayer> sender) {
        if (this.tps == null) {
            this.tps = new TPSHandler(this.plugin.getTasks());
            this.tps.scheduleSyncTask();
        }

        float lost = this.tps.lastLostTicks;

        ChatColor color;
        if (lost < 0.3) {
            color = ChatColor.AQUA;
        } else if (lost < 1.0) {
            color = ChatColor.DARK_AQUA;
        } else if (lost < 3.0) {
            color = ChatColor.GOLD;
        } else if (lost < 5.0) {
            color = ChatColor.YELLOW;
        } else if (lost < 10.0) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.DARK_RED;
        }

        sender.sendSuccess("Lost TPS: " + color + ChatColor.BOLD + lost + ChatColor.RESET + ChatColor.GREEN + ".");
    }

    private class TPSHandler extends Task {
        float lastLostTicks;
        Time lastTime = Time.now();

        public TPSHandler(TaskManager tasks) {
            super(tasks);
        }

        @Override
        public void onSecond(long seconds) {
            Time now = Time.now();

            this.lastLostTicks = now.minus(this.lastTime).toMillis() - 1F;
            this.lastTime = now;
        }
    }
}
