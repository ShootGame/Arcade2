package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import pl.themolka.arcade.channel.ChannelsModule;
import pl.themolka.arcade.channel.ChatChannel;
import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.match.FormatModule;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.spawn.SpawnsModule;

import java.util.Collection;

@ModuleInfo(id = "Teams",
        dependency = {
                MatchModule.class},
        loadBefore = {
                ChannelsModule.class,
                FiltersModule.class,
                KitsModule.class,
                SpawnsModule.class})
@ModuleVersion("1.0")
@FormatModule
public class TeamsModule extends Module<TeamsGame> {
    /** Permission node which allow to edit teams in runtime. */
    public static final String TEAMS_MANAGE_PERMISSION = "arcade.command.teams.manage";

    //
    // Commands
    //

    @CommandInfo(name = {"myteam", "mt"},
            description = "Show your current team",
            clientOnly = true,
            permission = "arcade.command.myteam")
    public void myTeam(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        Team team = this.getGameModule().getTeam(sender.getGamePlayer());
        if (team == null) {
            throw new CommandException("You are not in a team.");
        }

        sender.sendInfo("You are currently in " + team.getPrettyName() + ChatColor.GRAY + ".");

        if (team instanceof Observers) {
            sender.sendTip("Join the game by typing /join.");
        }
    }

    @CommandInfo(name = {"team", "t"},
            description = "Send a message to the teams chat channel",
            min = 1,
            usage = "<message...>",
            clientOnly = true,
            permission = ChatChannel.PERMISSION_NODE + "." + TeamChannel.TEAM_PERMISSION_NODE)
    public void team(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        Team team = this.getGameModule().getTeam(sender.getGamePlayer());
        if (team == null) {
            throw new CommandException("You are not in a team.");
        }

        team.getChannel().sendChatMessage(sender, context.getParams(0));
    }

    @CommandInfo(name = {"teams", "teamlist"},
            description = "Show all teams in this match or manage them",
            flags = {"xml"},
            usage = "[-xml|context...]",
            permission = "arcade.command.teams")
    public void teams(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        TeamCommands commands = this.getGameModule().getCommands();
        String command = context.getParam(0);

        if (sender.hasPermission(TEAMS_MANAGE_PERMISSION) && command != null) {
            switch (command.toLowerCase()) {
                case "clear":
                    commands.clearCommand(sender, context.getParam(1));
                    break;
                case "force":
                    commands.forceCommand(sender, context.getParam(1), context.getParam(2));
                    break;
                case "friendly":
                    commands.friendlyCommand(sender, context.getParam(1), context.getParamBoolean(2, true));
                    break;
                case "info":
                    commands.infoCommand(sender);
                    break;
                case "kick":
                    commands.kickCommand(sender, context.getParam(1));
                    break;
                case "min":
                    commands.minCommand(sender, context.getParam(1), context.getParamInt(2));
                    break;
                case "overfill":
                    commands.overfillCommand(sender, context.getParam(1), context.getParamInt(2));
                    break;
                case "paint":
                    commands.paintCommand(sender, context.getParam(1), context.getParam(2));
                    break;
                case "rename":
                    commands.renameCommand(sender, context.getParam(1), context.getParams(2));
                    break;
                case "slots":
                    commands.slotsCommand(sender, context.getParam(1), context.getParamInt(2));
                    break;
                default:
                    CommandUtils.sendTitleMessage(sender, "Teams Management");
                    sender.send(this.teamsCommand(context, "clear <team>", "Kick all players from a team"));
                    sender.send(this.teamsCommand(context, "force <player> <team>", "Force player to join a team"));
                    sender.send(this.teamsCommand(context, "friendly <team> <on|off>", "Set friendly-fire"));
                    sender.send(this.teamsCommand(context, "info", "Describe the teams"));
                    sender.send(this.teamsCommand(context, "kick <player>", "Kick player from the team"));
                    sender.send(this.teamsCommand(context, "min <team> <min>", "Set minimum amount of players"));
                    sender.send(this.teamsCommand(context, "overfill <team> <overfill>", "Set maximum team overfill"));
                    sender.send(this.teamsCommand(context, "paint <team> <color>", "Color a team"));
                    sender.send(this.teamsCommand(context, "rename <team> <name...>", "Rename a team"));
                    sender.send(this.teamsCommand(context, "slots <team> <slots>", "Set slots in a team"));
                    sender.sendTip("Type '" + Observers.OBSERVERS_KEY + "' if you want to specify observers.");
                    break;
            }

            return;
        }

        Collection<Team> teams = this.getGameModule().getTeams();
        CommandUtils.sendTitleMessage(sender, "Teams", Integer.toString(teams.size()));

        for (Team team : teams) {
            String message = ChatColor.GRAY + " - " + team.getPrettyName() + ChatColor.GRAY + " - " +
                    ChatColor.GOLD + ChatColor.BOLD + team.getOnlineMembers().size() + ChatColor.RESET;

            if (team.getSlots() != Integer.MAX_VALUE) {
                message += ChatColor.GRAY + "/" + team.getSlots();
            }

            if (context.hasFlag("xml")) {
                message += " " + this.teamsKeyValue("id", team.getId()) + ", " +
                        this.teamsKeyValue("chat-color", team.getChatColor()) + ", " +
                        this.teamsKeyValue("dye-color", team.getDyeColor()) + ", " +
                        this.teamsKeyValue("friendly-fire", team.isFriendlyFire()) + ", " +
                        this.teamsKeyValue("slots", team.getSlots()) + ", " +
                        this.teamsKeyValue("max-players", team.getMaxPlayers()) + ", " +
                        this.teamsKeyValue("min-players", team.getMinPlayers());
            }

            sender.send(message);
        }

        if (sender.hasPermission(TEAMS_MANAGE_PERMISSION)) {
            sender.sendTip("Type '/" + context.getLabel() + " help' if you want to mange the teams.");
        }
    }

    //
    // Command Utilities
    //

    private String teamsCommand(CommandContext context, String command, String description) {
        return ChatColor.YELLOW + "/" + context.getLabel() + " " + command + ChatColor.GRAY + " - " + description;
    }

    private String teamsKeyValue(String key, Object value) {
        return ChatColor.YELLOW + key + ChatColor.GRAY + "=" + ChatColor.RED + value + ChatColor.GRAY;
    }
}
