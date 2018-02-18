package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.CycleCountdown;
import pl.themolka.arcade.game.CycleStartEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.RestartCountdown;
import pl.themolka.arcade.map.MapQueue;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.task.Countdown;

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
    public void cancel(Sender sender, CommandContext context) {
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
    public void cycle(Sender sender, CommandContext context) {
        int paramSeconds = context.getParamInt(0, (int) CycleCountdown.DEFAULT_DURATION.getSeconds());

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
        if (seconds < 3) {
            seconds = 3;
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
            countdown.cancelCountdown();
            countdown.setDuration(Duration.ofSeconds(seconds));

            countdown.countSync();
        }
    }

    //
    // /recycle command
    //

    @CommandInfo(name = "recycle",
            description = "Re-cycle current map",
            usage = "[seconds]",
            permission = "arcade.command.cycle")
    public void recycle(Sender sender, CommandContext context) {
        if (this.plugin.getGames().getCurrentGame() == null) {
            throw new CommandException("Game is not running right now.");
        }

        MapQueue queue = this.plugin.getGames().getQueue();
        queue.setNextMap(this.plugin.getGames().getCurrentGame().getMap().getMapInfo());

        CycleCommandEvent commandEvent = new CycleCommandEvent(this.plugin, sender, context, queue.getNextMap());
        this.plugin.getEventBus().publish(commandEvent);

        if (commandEvent.isCanceled()) {
            return;
        }

        int seconds = context.getParamInt(0, (int) CycleCountdown.DEFAULT_DURATION.getSeconds());
        if (seconds < 3) {
            seconds = 3;
        }

        CycleStartEvent startEvent = new CycleStartEvent(this.plugin, queue.getNextMap(), seconds);
        this.plugin.getEventBus().publish(startEvent);

        this.plugin.getGames().setNextRestart(false);

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            for (Countdown countdown : game.getRunningCountdowns()) {
                countdown.cancelCountdown();
            }

            CycleCountdown countdown = this.plugin.getGames().getCycleCountdown();
            countdown.cancelCountdown();
            countdown.setDuration(Duration.ofSeconds(seconds));

            countdown.countSync();
        }
    }

    //
    // /restart command
    //

    @CommandInfo(name = "restart",
            description = "Cycle to next map",
            flags = {"f", "force"},
            usage = "[-force] [seconds]",
            permission = "arcade.command.restart")
    public void restart(Sender sender, CommandContext context) {
        int seconds = context.getParamInt(0, (int) RestartCountdown.DEFAULT_DURATION.getSeconds());
        if (seconds < 3) {
            seconds = 3;
        }

        boolean force = context.hasFlag("f") || context.hasFlag("force");
        CycleCommandEvent event = new CycleCommandEvent(this.plugin, sender, context, force);
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
            countdown.cancelCountdown();
            countdown.setDuration(Duration.ofSeconds(seconds));

            countdown.countSync();
        }
    }

    public static class CycleCommandEvent extends CommandEvent implements Cancelable {
        private boolean cancel;
        private final OfflineMap nextMap;
        private final boolean forceRestart;

        /**
         * Create a new map cycle.
         */
        public CycleCommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context, OfflineMap nextMap) {
            this(plugin, sender, context, nextMap, false);
        }

        /**
         * Create a new restart cycle.
         */
        public CycleCommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context, boolean forceRestart) {
            this(plugin, sender, context, null, forceRestart);
        }

        CycleCommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context,
                                 OfflineMap nextMap, boolean forceRestart) {
            super(plugin, sender, context);

            this.nextMap = nextMap;
            this.forceRestart = forceRestart;
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

        public boolean isForceRestart() {
            return this.isNextRestart() && this.forceRestart;
        }

        public boolean isNextRestart() {
            return this.nextMap == null;
        }
    }
}
