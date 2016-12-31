package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.session.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ModuleInfo(id = "teams", dependency = {MatchModule.class})
public class TeamsModule extends Module<TeamsGame> {
    public static final String METADATA_OBSERVERS = "observers";
    public static final String METADATA_TEAM = "team";

    @Override
    public TeamsGame buildGameModule(Element xml) throws JDOMException {
        Match match = (Match) this.getGame().getMetadata(MatchModule.class, MatchModule.METADATA_MATCH);
        ObserversTeam observers = new ObserversTeam(this.getPlugin(), match);
        List<Team> teams = new ArrayList<>();

        Element observersElement = xml.getChild("observers");
        if (observersElement != null) {
            observers = XMLObserversTeam.parse(observersElement);
        }

        for (Element teamElement : xml.getChildren("team")) {
            Team team = XMLTeam.parse(teamElement);
            if (team != null) {
                teams.add(team);
            }
        }

        return new TeamsGame(observers, teams);
    }

    @CommandInfo(name = {"myteam", "team", "mt"},
            description = "Show your current team",
            userOnly = true,
            permission = "arcade.command.myteam")
    public void myTeam(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        Team team = this.getGameModule().getTeam(sender.getRepresenter().getGamePlayer());
        sender.sendInfo("You are currently in " + team.getPrettyName() + ChatColor.YELLOW + ".");

        if (team instanceof ObserversTeam) {
            sender.getRepresenter().sendTip("Join the game by typing /join.");
        }
    }

    @CommandInfo(name = {"teams", "teamlist"},
            description = "Show all teams in this match",
            flags = {"xml"},
            usage = "[-xml]",
            permission = "arcade.command.teams",
            completer = "teamsCompleter")
    public void teams(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Teams module is not enabled in this game.");
        }

        Collection<Team> teams = this.getGameModule().getTeams();
        Commands.sendTitleMessage(sender, "Teams", Integer.toString(teams.size()));

        for (Team team : teams) {
            String message = ChatColor.GRAY + " - " + team.getPrettyName() + ChatColor.GRAY + " - " + ChatColor.GOLD +
                    ChatColor.BOLD + team.getMembers().size() + ChatColor.RESET + ChatColor.GRAY + "/" + team.getSlots();

            if (context.hasFlag("xml")) {
                message += " " + this.teamsKeyValue("id", team.getId()) + ", " +
                        this.teamsKeyValue("color", team.getColor()) + ", " +
                        this.teamsKeyValue("dye-color", team.getDyeColor());
            }

            sender.send(message);
        }
    }

    public List<String> teamsCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        return Collections.singletonList("-xml");
    }

    private String teamsKeyValue(String key, Object value) {
        return ChatColor.YELLOW + key + ChatColor.GRAY + "=" + ChatColor.RED + value.toString() + ChatColor.GRAY;
    }
}
