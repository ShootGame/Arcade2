package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

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
    public void gameInfo(Sender sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("No game running right now. Please try again later.");
        }

        CommandUtils.sendTitleMessage(sender, "Game", "#" + (game.getGameId() + 1));
        this.plugin.getEventBus().publish(new GameCommandEvent(this.plugin, sender, context));
    }

    //
    // /join command
    //

    @CommandInfo(name = {"join", "play"},
            description = "Join the game",
            flags = {"a", "auto"},
            usage = "[-auto] [context...]",
            clientOnly = true,
            permission = "arcade.command.join",
            completer = "joinCompleter")
    public void join(Sender sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        String param = context.getParam(0);
        if (param != null) {
            boolean observers = param.equalsIgnoreCase("obs") || param.equalsIgnoreCase("observers");
            if (observers) {
                this.leave(sender, context);
                return;
            }
        }

        boolean auto = param == null || context.hasFlag("a") || context.hasFlag("auto");
        this.plugin.getEventBus().publish(new JoinCommandEvent(this.plugin, sender, context, auto));
    }

    public List<String> joinCompleter(Sender sender, CommandContext context) {
        if (this.plugin.getGames().getCurrentGame() == null) {
            throw new CommandException("Could not join the game right now. Please try again later.");
        }

        JoinCompleterEvent event = new JoinCompleterEvent(this.plugin, sender, context);
        this.plugin.getEventBus().publish(event);

        return event.getResults();
    }

    //
    // /leave command
    //

    @CommandInfo(name = {"leave", "quit"},
            description = "Leave the game",
            clientOnly = true,
            permission = "arcade.command.leave")
    public void leave(Sender sender, CommandContext context) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not leave the game right now. Please try again later.");
        }

        this.plugin.getEventBus().publish(new LeaveCommandEvent(this.plugin, sender, context));
    }

    public static class JoinCommandEvent extends CommandEvent implements Cancelable {
        private final boolean auto;
        private boolean cancel;
        private boolean joined;

        public JoinCommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context, boolean auto) {
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

        public GamePlayer getJoinGamePlayer() {
            return this.getJoinPlayer().getGamePlayer();
        }

        public ArcadePlayer getJoinPlayer() {
            return this.getSender().getPlayer();
        }

        public boolean hasJoined() {
            return this.joined;
        }

        public boolean isAuto() {
            return this.auto;
        }

        public void setJoined(boolean joined) {
            this.joined = joined;
        }
    }

    public static class GameCommandEvent extends CommandEvent {
        public GameCommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
            super(plugin, sender, context);
        }
    }

    public static class JoinCompleterEvent extends CommandCompleterEvent {
        public JoinCompleterEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
            super(plugin, sender, context);
        }
    }

    public static class LeaveCommandEvent extends CommandEvent {
        public LeaveCommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
            super(plugin, sender, context);
        }

        public GamePlayer getLeaveGamePlayer() {
            return this.getLeavePlayer().getGamePlayer();
        }

        public ArcadePlayer getLeavePlayer() {
            return this.getSender().getPlayer();
        }
    }
}
