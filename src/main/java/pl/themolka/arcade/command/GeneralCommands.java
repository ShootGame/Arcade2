package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.CycleCountdown;
import pl.themolka.arcade.game.CycleStartEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.event.Cancelable;

import java.time.Duration;

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
            flags = "force",
            usage = "[-force]",
            permission = "arcade.command.cancel")
    public void cancel(ArcadeSession sender, CommandContext context) {
        boolean paramForce = context.hasFlag("f") || context.hasFlag("force");

        Game game = this.plugin.getGames().getCurrentGame();

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

        int seconds = paramSeconds;
        if (seconds < 5) {
            seconds = 5;
        }

        CycleStartEvent startEvent = new CycleStartEvent(this.plugin, nextMap, seconds);
        this.plugin.getEvents().post(startEvent);

        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            CycleCountdown countdown = new CycleCountdown(this.plugin, Duration.ofSeconds(seconds));
            game.addCountdown(countdown);
        }
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
}
