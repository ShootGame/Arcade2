package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.session.Session;

import java.util.List;

public class GameCommands {
    private final ArcadePlugin plugin;

    public GameCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // /join command
    //

    @CommandInfo(name = {"join", "play"},
            description = "Join the game",
            usage = "[context]",
            userOnly = true,
            permission = "arcade.command.join",
            completer = "joinCompleter")
    public void joinCommand(Session<ArcadePlayer> sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        String param = context.getParam(0);
        if (param != null) {
            boolean observers = param.equalsIgnoreCase("o") || param.equalsIgnoreCase("obs") || param.equalsIgnoreCase("observers");
            if (observers) {
                this.leaveCommand(sender, context);
                return;
            }
        }

        this.plugin.getEvents().post(new JoinCommandEvent(this.plugin, (ArcadeSession) sender, context, param == null));
    }

    public List<String> joinCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        if (this.plugin.getGames().getCurrentGame() == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        JoinCompleterEvent event = new JoinCompleterEvent(this.plugin, (ArcadeSession) sender, context);
        this.plugin.getEvents().post(event);

        return event.getResults();
    }

    @CommandInfo(name = {"leave", "quit"},
            description = "Leave the game",
            userOnly = true,
            permission = "arcade.command.quit")
    public void leaveCommand(Session<ArcadePlayer> sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not leave the game right now. Please try again later.");
        }

        this.plugin.getEvents().post(new LeaveCommandEvent(this.plugin, (ArcadeSession) sender, context));
    }

    public static class JoinCommandEvent extends CommandEvent {
        private final boolean auto;

        public JoinCommandEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context, boolean auto) {
            super(plugin, plugin.getGames().getCurrentGame(), sender, context);

            this.auto = auto;
        }

        public ArcadePlayer getJoinPlayer() {
            return this.getSender().getRepresenter();
        }

        public boolean isAuto() {
            return this.auto;
        }
    }

    public static class JoinCompleterEvent extends CommandCompleterEvent {
        public JoinCompleterEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context) {
            super(plugin, plugin.getGames().getCurrentGame(), sender, context);
        }
    }

    public static class LeaveCommandEvent extends CommandEvent {
        public LeaveCommandEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context) {
            super(plugin, plugin.getGames().getCurrentGame(), sender, context);
        }
    }
}
