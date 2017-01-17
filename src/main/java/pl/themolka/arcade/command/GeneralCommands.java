package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.CycleCountdown;
import pl.themolka.arcade.game.CycleStartEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.RestartCountdown;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.task.Countdown;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.event.Cancelable;
import pl.themolka.commons.session.Session;

import java.time.Duration;
import java.util.List;

public class GeneralCommands {
    private final ArcadePlugin plugin;

    public GeneralCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // /cancel command
    //

    @CommandInfo(name = "cancel",
            description = "Cancel current countdown",
            flags = {"f", "force"},
            usage = "[-force]",
            permission = "arcade.command.cancel")
    public void cancel(Session<ArcadePlayer> sender, CommandContext context) {
        boolean paramForce = context.hasFlag("f") || context.hasFlag("force");

        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            throw new CommandException("Could not cancel right now. Please try again later.");
        }

        List<Countdown> countdowns = game.getRunningCountdowns();
        if (countdowns.isEmpty()) {
            throw new CommandException("No countdowns running right now.");
        }

        int i = 0;
        for (Countdown countdown : game.getRunningCountdowns()) {
            if (countdown.cancelCountdown()) {
                countdown.setForcedCancel(paramForce);
                i++;
            }
        }

        if (i != 0) {
            sender.sendSuccess("Successfully canceled " + i + " countdown(s).");
        } else {
            throw new CommandException("No countdowns could be canceled right now.");
        }
    }

    //
    // /cycle command
    //

    @CommandInfo(name = "cycle",
            description = "Cycle to next map",
            usage = "[seconds]",
            permission = "arcade.command.cycle")
    public void cycle(Session<ArcadePlayer> sender, CommandContext context) {
        int paramSeconds = context.getParamInt(0);

        OfflineMap nextMap = this.plugin.getGames().getQueue().getNextMap();
        if (nextMap == null) {
            String reason = "The map queue is empty.";
            if (sender.hasPermission("arcade.command.setnext")) {
                reason += " Set next map using /setnext <map...>.";
            }

            throw new CommandException(reason);
        }

        CycleCommandEvent commandEvent = new CycleCommandEvent(this.plugin, sender, context, nextMap);
        this.plugin.getEventBus().publish(commandEvent);

        if (commandEvent.isCanceled()) {
            return;
        }

        int seconds = paramSeconds;
        if (seconds < 5) {
            seconds = 5;
        }

        CycleStartEvent startEvent = new CycleStartEvent(this.plugin, nextMap, seconds);
        this.plugin.getEventBus().publish(startEvent);

        this.plugin.getGames().setNextRestart(false);

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            for (Countdown countdown : game.getRunningCountdowns()) {
                countdown.cancelCountdown();
            }

            CycleCountdown countdown = this.plugin.getGames().getCycleCountdown();
            countdown.setDuration(Duration.ofSeconds(seconds));

            countdown.countSync();
        }
    }

    //
    // /restart command
    //

    @CommandInfo(name = "restart",
            description = "Cycle to next map",
            usage = "[seconds]",
            permission = "arcade.command.restart")
    public void restart(Session<ArcadePlayer> sender, CommandContext context) {
        int seconds = context.getParamInt(0);
        if (seconds < 5) {
            seconds = 5;
        }

        CycleCommandEvent event = new CycleCommandEvent(this.plugin, sender, context, null);
        this.plugin.getEventBus().publish(event);

        if (event.isCanceled()) {
            return;
        }

        this.plugin.getGames().setNextRestart(true);

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            for (Countdown countdown : game.getRunningCountdowns()) {
                countdown.cancelCountdown();
            }

            RestartCountdown countdown = this.plugin.getGames().getRestartCountdown();
            countdown.setDuration(Duration.ofSeconds(seconds));

            countdown.countSync();
        }
    }

    public static class CycleCommandEvent extends CommandEvent implements Cancelable {
        private boolean cancel;
        private final OfflineMap nextMap;

        public CycleCommandEvent(ArcadePlugin plugin, Session<ArcadePlayer> sender, CommandContext context, OfflineMap nextMap) {
            super(plugin, sender, context);

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

        boolean isNextRestart() {
            return this.nextMap == null;
        }
    }
}
