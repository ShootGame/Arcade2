package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.Collection;

public class TeamCommands {
    private final TeamsGame game;

    public TeamCommands(TeamsGame game) {
        this.game = game;
    }

    //
    // Commands
    //

    public void clearCommand(Sender sender, String teamId) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot clear observers.");
        }

        Observers observers = this.game.getMatch().getObservers();

        int result = 0;
        for (GamePlayer player : new ArrayList<>(team.getOnlineMembers())) {
            observers.joinForce(player);
            result++;
        }

        if (result > 0) {
            sender.sendSuccess(team.getName() + " has been cleared (" + result + " players) and moved to " +
                    observers.getName() + ".");
        } else {
            sender.sendError("No players to clear.");
        }
    }

    public void forceCommand(Sender sender, String username, String teamId) {
        GamePlayer player = this.fetchPlayer(username);
        Team team = this.fetchTeam(teamId);

        if (team.contains(player)) {
            throw new CommandException(player.getUsername() + " is already member of " + team.getName() + ".");
        }

        team.joinForce(player);
        sender.sendSuccess(player.getUsername() + " has been moved to " + team.getName() + ".");
    }

    public void friendlyCommand(Sender sender, String teamId, boolean friendly) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        }

        if (friendly == team.isFriendlyFire()) {
            if (friendly) {
                throw new CommandException(team.getName() + " is already in friendly-fire.");
            } else {
                throw new CommandException(team.getName() + " is already not in friendly-fire");
            }
        }

        Team oldState = new Team(team);
        team.setFriendlyFire(friendly);

        this.callEditEvent(team, oldState, TeamEditEvent.Reason.FRIENDLY_FIRE);
        if (friendly) {
            sender.sendSuccess(oldState.getName() + " is now in friendly-fire.");
        } else {
            sender.sendSuccess(oldState.getName() + " is now not in friendly-fire.");
        }
    }

    public void infoCommand(Sender sender) {
        Collection<Team> teams = this.game.getTeams();

        CommandUtils.sendTitleMessage(sender, "Teams", Integer.toString(teams.size()));
        for (Team team : teams) {
            sender.send(String.format("%s - %s/%s - %s minimal to play and %s overfill",
                    team.getPrettyName() + ChatColor.GRAY,
                    ChatColor.GOLD.toString() + team.getOnlineMembers().size() + ChatColor.GRAY,
                    Integer.toString(team.getSlots()),
                    ChatColor.GREEN.toString() + team.getMinPlayers() + ChatColor.GRAY,
                    ChatColor.RED.toString() + team.getMaxPlayers() + ChatColor.GRAY));
        }
    }

    public void kickCommand(Sender sender, String username) {
        GamePlayer player = this.fetchPlayer(username);
        Team team = this.game.getTeam(player);

        if (team.isObservers()) {
            throw new CommandException("Cannot kick from observers.");
        }

        team.leaveForce(player);
        team.getMatch().getObservers().joinForce(player);
        sender.sendSuccess(player.getUsername() + " has been kicked from " + team.getName() + ".");
    }

    public void minCommand(Sender sender, String teamId, int min) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        } else if (min < 0) {
            throw new CommandException("Number cannot be negative.");
        }

        Team oldState = new Team(team);
        team.setMinPlayers(min);

        this.callEditEvent(team, oldState, TeamEditEvent.Reason.MIN_PLAYERS);
        sender.sendSuccess(oldState.getName() + " has been edited.");
    }

    public void overfillCommand(Sender sender, String teamId, int overfill) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        }

        // set to unlimited if zero or negative
        int max = Integer.MAX_VALUE;
        if (overfill > 0) {
            max = overfill;
        }

        Team oldState = new Team(team);
        team.setMaxPlayers(max);

        if (max > team.getSlots()) {
            team.setSlots(max); // slots
        }

        this.callEditEvent(team, oldState, TeamEditEvent.Reason.MAX_PLAYERS);
        sender.sendSuccess(oldState.getName() + " has been edited.");
    }

    public void paintCommand(Sender sender, String teamId, String paint) {
        Team team = this.fetchTeam(teamId);
        ChatColor color = null;

        if (paint != null && !paint.isEmpty()) {
            try {
                color = ChatColor.valueOf(XMLParser.parseEnumValue(paint));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (color == null) {
            StringBuilder colors = new StringBuilder();
            for (int i = 0; i < ChatColor.values().length; i++) {
                ChatColor value = ChatColor.values()[i];
                if (i != 0) {
                    colors.append(", ");
                }

                ChatColor result = ChatColor.RED;
                if (!value.equals(ChatColor.MAGIC)) {
                    result = value;
                }

                colors.append(result).append(value.name().toLowerCase().replace("_", "-"))
                        .append(ChatColor.RESET).append(ChatColor.RED);
            }

            throw new CommandException("Available colors: " + colors.toString() + ".");
        }

        Team oldState = new Team(team);
        team.setChatColor(color);

        this.callEditEvent(team, oldState, TeamEditEvent.Reason.PAINT);
        sender.sendSuccess(oldState.getName() + " has been painted from " +
                oldState.getChatColor().name().toLowerCase().replace("_", "-") + " to " +
                team.getChatColor().name().toLowerCase().replace("_", "-") + ".");
    }

    public void renameCommand(Sender sender, String teamId, String name) {
        Team team = this.fetchTeam(teamId);
        if (name == null) {
            throw new CommandException("New name not given.");
        } else if (name.length() > Team.NAME_MAX_LENGTH) {
            throw new CommandException("Name too long (greater than " + Team.NAME_MAX_LENGTH + " characters).");
        } else if (team.getName().equals(name)) {
            throw new CommandException("Already named '" + team.getName() + "'.");
        }

        Team oldState = new Team(team);
        team.setName(name);

        this.callEditEvent(team, oldState, TeamEditEvent.Reason.RENAME);
        sender.sendSuccess(oldState.getName() + " has been renamed to " + team.getName() + ".");
    }

    public void slotsCommand(Sender sender, String teamId, int slots) {
        Team team = this.fetchTeam(teamId);
        if (team.isObservers()) {
            throw new CommandException("Cannot edit observers.");
        }

        // set to unlimited if zero or negative
        int max = Integer.MAX_VALUE;
        if (slots > 0) {
            max = slots;
        }

        Team oldState = new Team(team);
        team.setSlots(max);

        if (max > team.getMaxPlayers()) {
            team.setMaxPlayers(max); // overfill
        }

        this.callEditEvent(team, oldState, TeamEditEvent.Reason.SLOTS);
        sender.sendSuccess(oldState.getName() + " has been edited.");
    }

    //
    // Command Utilities
    //

    private void callEditEvent(Team newState, Team oldState, TeamEditEvent.Reason reason) {
        this.game.getPlugin().getEventBus().publish(new TeamEditEvent(
                this.game.getPlugin(), newState, oldState, reason));
    }

    private GamePlayer fetchPlayer(String player) {
        if (player != null && !player.isEmpty()) {
            GamePlayer result = this.game.getGame().findPlayer(player);
            if (result != null) {
                return result;
            }
        }

        throw new CommandException("Player not found.");
    }

    private Team fetchTeam(String team) {
        if (team != null && !team.isEmpty()) {
            Team result = this.game.findTeamById(team);
            if (result != null) {
                return result;
            }
        }

        throw new CommandException("Team not found.");
    }
}
