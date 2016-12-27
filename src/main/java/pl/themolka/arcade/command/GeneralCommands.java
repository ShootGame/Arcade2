package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.CycleStartEvent;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.event.Cancelable;

import java.util.List;

public class GeneralCommands {
    private final ArcadePlugin plugin;

    public GeneralCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // /cycle command
    //

    @CommandInfo(name = "cycle",
            description = "Cycle to next map",
            usage = "[seconds]",
            permission = "arcade.command.cycle")
    public void cycle(ArcadeSession sender, CommandContext context) {
        int paramSeconds = context.getParamInt(0);

        OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
        CycleCommandEvent commandEvent = new CycleCommandEvent(this.plugin, sender, context, nextMap);
        this.plugin.getEvents().post(commandEvent);

        if (commandEvent.isCanceled()) {
            return;
        }

        CycleStartEvent startEvent = new CycleStartEvent(this.plugin, nextMap);
        this.plugin.getEvents().post(startEvent);

        // TODO start countdown
    }

    public static class CycleCommandEvent extends CommandEvent implements Cancelable {
        private boolean cancel;
        private final OfflineMap nextMap;

        public CycleCommandEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context, OfflineMap nextMap) {
            super(plugin, plugin.getGames().getCurrentGame(), sender, context);

            this.nextMap = nextMap;
        }

        @Override
        public boolean isCanceled() {
            return this.cancel;
        }

        @Override
        public void setCanceled(boolean cancel) {
            this.cancel = cancel;
        }

        public OfflineMap getNextMap() {
            return this.nextMap;
        }
    }

    //
    // /join command
    //

    @CommandInfo(name = {"join", "play"},
            description = "Join the game",
            usage = "[context]",
            permission = "arcade.command.join",
            completer = "joinCompleter")
    public void join(ArcadeSession sender, CommandContext context) {
        if (this.plugin.getGames().getCurrentGame() == null) {
            throw new CommandException("Could not join right now. Please try again later.");
        }

        JoinCommandEvent event = new JoinCommandEvent(this.plugin, sender, context, context.getParam(0) == null);
        this.plugin.getEvents().post(event);
    }

    public List<String> joinCompleter(ArcadeSession sender, CommandContext context) {
        if (this.plugin.getGames().getCurrentGame() == null) {
            throw new CommandException("Could not join right now. Please try again later.");
        }

        JoinCompleterEvent event = new JoinCompleterEvent(this.plugin, sender, context);
        this.plugin.getEvents().post(event);

        return event.getResults();
    }

    public static class JoinCommandEvent extends CommandEvent {
        private final boolean auto;

        public JoinCommandEvent(ArcadePlugin plugin, ArcadeSession sender, CommandContext context, boolean auto) {
            super(plugin, plugin.getGames().getCurrentGame(), sender, context);

            this.auto = auto;
        }

        public ArcadePlayer getJoinPlayer() {
            ArcadeSession sender = this.getSender();
            if (sender.isConsole()) {
                return null;
            }

            return sender.getRepresenter();
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
}
