package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.jdom2.Element;
import org.jdom2.JDOMException;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ModuleInfo(id = "teams", dependency = MatchModule.class, loadBefore = {FiltersModule.class, KitsModule.class})
public class TeamsModule extends Module<TeamsGame> {
    public static final String METADATA_TEAM = "Team";
    public static final String METADATA_TEAMS = "Teams";

    @Override
    public TeamsGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<Team> teams = new ArrayList<>();
        for (Element teamElement : xml.getChildren("team")) {
            Team team = XMLTeam.parse(teamElement, this.getPlugin());
            if (team != null) {
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

        sender.sendInfo("You are currently in " + team.getPrettyName() + ChatColor.YELLOW + ".");

        if (team instanceof Observers) {
            sender.sendTip("Join the game by typing /join.");
        }
    }

    @CommandInfo(name = {"teams", "teamlist"},
            description = "Show all teams in this match",
            flags = {"xml"},
            usage = "[-xml]",
            permission = "arcade.command.teams",
            completer = "teamsCompleter")
    public void teams(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        TeamsGame game = this.getGameModule();
        Collection<Team> teams = game.getTeams();
        CommandUtils.sendTitleMessage(sender, "Teams", Integer.toString(teams.size()));

        for (Team team : teams) {
            String message = ChatColor.GRAY + " - " + team.getPrettyName() + ChatColor.GRAY + " - " +
                    ChatColor.GOLD + ChatColor.BOLD + team.getOnlineMembers().size() + ChatColor.RESET +
                    ChatColor.GRAY + "/" + team.getSlots();

            if (context.hasFlag("xml")) {
                message += " " + this.teamsKeyValue("id", team.getId()) + ", " +
                        this.teamsKeyValue("color", team.getColor()) + ", " +
                        this.teamsKeyValue("dye-color", team.getDyeColor()) + ", " +
                        this.teamsKeyValue("friendly-fire", team.isFriendlyFire()) + ", " +
                        this.teamsKeyValue("max-players", team.getMaxPlayers()) + ", " +
                        this.teamsKeyValue("min-players", team.getMinPlayers());
            }

            sender.send(message);
        }
    }

    public List<String> teamsCompleter(Sender sender, CommandContext context) {
        return Collections.singletonList("-xml");
    }

    private String teamsKeyValue(String key, Object value) {
        return ChatColor.YELLOW + key + ChatColor.GRAY + "=" + ChatColor.RED + value + ChatColor.GRAY;
    }
}
