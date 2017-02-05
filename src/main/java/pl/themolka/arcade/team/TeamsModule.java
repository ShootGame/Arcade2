package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.channel.ChannelsModule;
import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.region.RegionsModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ModuleInfo(id = "teams",
        dependency = MatchModule.class,
        loadBefore = {ChannelsModule.class, FiltersModule.class, KitsModule.class, RegionsModule.class})
public class TeamsModule extends Module<TeamsGame> {
    public static final String METADATA_TEAM = "Team";
    public static final String METADATA_TEAMS = "Teams";

    @Override
    public TeamsGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<Team> teams = new ArrayList<>();
        for (Element teamElement : xml.getChildren("team")) {
            Team team = XMLTeam.parse(teamElement, this.getPlugin());
            if (team != null) {
                team.setBukkit(Team.createBukkitTeam(game.getScoreboard().getScoreboard(), team));
                teams.add(team);
            }
        }

        return new TeamsGame(teams);
    }

    @CommandInfo(name = {"myteam", "team", "mt"},
            description = "Show your current team",
            clientOnly = true,
            permission = "arcade.command.myteam")
    public void myTeam(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        TeamsGame game = this.getGameModule();
        Team team = game.getTeam(sender.getGamePlayer());

        if (team == null) {
            team = game.getMatch().getObservers();
            team.join(sender.getGamePlayer(), false);
        }

        sender.sendInfo("You are currently in " + team.getPrettyName() + ChatColor.GRAY + ".");

        if (team instanceof Observers) {
            sender.sendTip("Join the game by typing /join.");
        }
    }

    @CommandInfo(name = {"teams", "teamlist"},
            description = "Show all teams in this match or manage them",
            flags = {"xml"},
            usage = "[-xml|context...]",
            permission = "arcade.command.teams",
            completer = "teamsCompleter")
    public void teams(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        TeamsGame game = this.getGameModule();
        if (sender.hasPermission("arcade.command.teams.manage") && context.getParam(0) != null) {
            switch (context.getParam(0).toLowerCase()) {
                case "clear":
                    game.clearCommand(sender, context.getParam(1));
                    break;
                case "force":
                    game.forceCommand(sender, context.getParam(1), context.getParam(2));
                    break;
                case "friendly":
                    game.friendlyCommand(sender, context.getParam(1), context.getParamBoolean(2, true));
                    break;
                case "kick":
                    game.kickCommand(sender, context.getParam(1));
                    break;
                case "min":
                    game.minCommand(sender, context.getParam(1), context.getParamInt(2));
                    break;
                case "overfill":
                    game.overfillCommand(sender, context.getParam(1), context.getParamInt(2));
                    break;
                case "paint":
                    game.paintCommand(sender, context.getParam(1), context.getParam(2));
                    break;
                case "rename":
                    game.renameCommand(sender, context.getParam(1), context.getParams(2));
                    break;
                case "slots":
                    game.slotsCommand(sender, context.getParam(1), context.getParamInt(2));
                    break;
                default:
                    CommandUtils.sendTitleMessage(sender, "Teams Management");
                    sender.send(this.teamsCommand(context, "clear <team>", "Kick all players from a team"));
                    sender.send(this.teamsCommand(context, "force <player> <team>", "Force player to join a team"));
                    sender.send(this.teamsCommand(context, "friendly <team> <on|off>", "Set friendly-fire"));
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

        Collection<Team> teams = game.getTeams();
        CommandUtils.sendTitleMessage(sender, "Teams", Integer.toString(teams.size()));

        for (Team team : teams) {
            String message = ChatColor.GRAY + " - " + team.getPrettyName() + ChatColor.GRAY + " - " +
                    ChatColor.GOLD + ChatColor.BOLD + team.getOnlineMembers().size() + ChatColor.RESET;

            if (team.getSlots() != Integer.MAX_VALUE) {
                message += ChatColor.GRAY + "/" + team.getSlots();
            }

            if (context.hasFlag("xml")) {
                message += " " + this.teamsKeyValue("id", team.getId()) + ", " +
                        this.teamsKeyValue("color", team.getColor()) + ", " +
                        this.teamsKeyValue("dye-color", team.getDyeColor()) + ", " +
                        this.teamsKeyValue("friendly-fire", team.isFriendlyFire()) + ", " +
                        this.teamsKeyValue("slots", team.getSlots()) + ", " +
                        this.teamsKeyValue("max-players", team.getMaxPlayers()) + ", " +
                        this.teamsKeyValue("min-players", team.getMinPlayers());
            }

            sender.send(message);
        }
    }

    public List<String> teamsCompleter(Sender sender, CommandContext context) {
        String requestedContext = context.getParams(0);
        if (requestedContext == null) {
            requestedContext = "";
        }

        List<String> completions = Collections.singletonList("-xml");
        List<String> results = new ArrayList<>();
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(requestedContext.toLowerCase())) {
                results.add(completion);
            }
        }

        return results;
    }

    private String teamsCommand(CommandContext context, String command, String description) {
        return ChatColor.YELLOW + "/" + context.getLabel() + " " + command + ChatColor.GRAY + " - " + description;
    }

    private String teamsKeyValue(String key, Object value) {
        return ChatColor.YELLOW + key + ChatColor.GRAY + "=" + ChatColor.RED + value + ChatColor.GRAY;
    }
}
