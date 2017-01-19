package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
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
    // /game command
    //

    @CommandInfo(name = {"gameinfo", "game"},
            description = "Describe the game",
            permission = "arcade.command.gameinfo")
    public void gameInfo(Session<ArcadePlayer> sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        Commands.sendTitleMessage(sender, "Game", "#" + this.plugin.getGames().getGameId());
        this.plugin.getEventBus().publish(new GameCommandEvent(this.plugin, sender, context));
    }

    //
    // /join command
    //

    @CommandInfo(name = {"join", "play"},
            description = "Join the game",
            usage = "[context...]",
            userOnly = true,
            permission = "arcade.command.join",
            completer = "joinCompleter")
    public void join(Session<ArcadePlayer> sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        String param = context.getParam(0);
        if (param != null) {
            boolean observers = param.equalsIgnoreCase("o") || param.equalsIgnoreCase("obs") || param.equalsIgnoreCase("observers");
            if (observers) {
                this.leave(sender, context);
                return;
            }
        }

        this.plugin.getEventBus().publish(new JoinCommandEvent(this.plugin, (ArcadeSession) sender, context, param == null));
    }

    public List<String> joinCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        if (this.plugin.getGames().getCurrentGame() == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        JoinCompleterEvent event = new JoinCompleterEvent(this.plugin, (ArcadeSession) sender, context);
        this.plugin.getEventBus().publish(event);

        return event.getResults();
    }

    //
    // /leave command
    //

    @CommandInfo(name = {"leave", "quit"},
            description = "Leave the game",
            userOnly = true,
            permission = "arcade.command.leave")
    public void leave(Session<ArcadePlayer> sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not leave the game right now. Please try again later.");
        }

        this.plugin.getEventBus().publish(new LeaveCommandEvent(this.plugin, (ArcadeSession) sender, context));
    }

    public static class JoinCommandEvent extends CommandEvent implements Cancelable {
        private final boolean auto;
        private boolean cancel;

        public JoinCommandEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context, boolean auto) {
            super(plugin, sender, context);

            this.auto = auto;
        }

        @Override
        public boolean isCanceled() {
            return this.cancel;
        }

        @Override
        public void setCanceled(boolean cancel) {
            this.cancel = cancel;
        }

        public ArcadePlayer getJoinPlayer() {
            return this.getSender().getRepresenter();
        }

        public boolean isAuto() {
            return this.auto;
        }
    }

    public static class GameCommandEvent extends CommandEvent {
        public GameCommandEvent(ArcadePlugin plugin, Session<ArcadePlayer> sender, CommandContext context) {
            super(plugin, sender, context);
        }
    }

    public static class JoinCompleterEvent extends CommandCompleterEvent {
        public JoinCompleterEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context) {
            super(plugin, sender, context);
        }

        public ArcadePlayer getJoinPlayer() {
            return this.getSender().getRepresenter();
        }
    }

    public static class LeaveCommandEvent extends CommandEvent {
        public LeaveCommandEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context) {
            super(plugin, sender, context);
        }

        public ArcadePlayer getLeavePlayer() {
            return this.getSender().getRepresenter();
        }
    }
}
