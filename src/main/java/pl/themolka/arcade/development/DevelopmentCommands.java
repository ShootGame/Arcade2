package pl.themolka.arcade.development;

import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.CycleStartEvent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.task.Countdown;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentCommands {
    private final Development development;

    public DevelopmentCommands(Development development) {
        this.development = development;
    }

    //
    // /cyclenow command
    //

    @CommandInfo(name = {"cyclenow"},
            description = "Cycle now to the given map",
            flags = {"c", "current",
                    "n", "next"},
            usage = "<map...>",
            permission = "arcade.command.cyclenow")
    public void cycleNow(Sender sender, CommandContext context) {
        boolean paramCurrent = context.hasFlag("c") || context.hasFlag("current");
        boolean paramNext = context.hasFlag("n") || context.hasFlag("next");
        String paramMap = context.getParams(0);

        List<OfflineMap> results = new ArrayList<>();
        if (paramCurrent || context.getArgs().length == 0) {
            Game game = this.development.getPlugin().getGames().getCurrentGame();
            if (game == null) {
                throw new CommandException("No game running right now.");
            }

            results.add(game.getMap().getMapInfo());
        } else if (paramNext) {
            OfflineMap next = this.development.getPlugin().getGames().getQueue().getNextMap();

            if (next == null) {
                String reason = "The map queue is empty.";
                if (sender.hasPermission("arcade.command.setnext")) {
                    reason += " Set next map using /setnext <map...>.";
                }

                throw new CommandException(reason);
            }

            results.add(next);
        } else {
            results.addAll(this.development.getPlugin().getMaps().findMap(paramMap));
        }

        if (results.isEmpty()) {
            throw new CommandException("No results found.");
        } else if (results.size() > 1) {
            sender.sendError("Found " + results.size() + " results. Cycling to the best one...");
        }

        OfflineMap target = results.get(0);

        GeneralCommands.CycleCommandEvent commandEvent = new GeneralCommands.CycleCommandEvent(
                this.development.getPlugin(), sender, context, target);
        this.development.getPlugin().getEventBus().publish(commandEvent);

        if (!commandEvent.isCanceled()) {
            sender.sendSuccess("Cycling now to " + target.getName() + "...");

            Game game = this.development.getPlugin().getGames().getCurrentGame();
            for (Countdown countdown : game.getRunningCountdowns()) {
                countdown.cancelCountdown();
            }

            this.development.getPlugin().getGames().getCycleCountdown().cancelCountdown();
            this.development.getPlugin().getGames().cycle(target);
        }
    }

    //
    // /recyclenow command
    //

    @CommandInfo(name = {"recyclenow"},
            description = "Re-cycle now current map",
            permission = "arcade.command.cyclenow")
    public void recycleNow(Sender sender, CommandContext context) {
        if (this.development.getPlugin().getGames().getCurrentGame() == null) {
            throw new CommandException("Game is not running right now.");
        }

        OfflineMap target = this.development.getPlugin().getGames().getCurrentGame().getMap().getMapInfo();

        GeneralCommands.CycleCommandEvent commandEvent = new GeneralCommands.CycleCommandEvent(
                this.development.getPlugin(), sender, context, target);
        this.development.getPlugin().getEventBus().publish(commandEvent);

        if (commandEvent.isCanceled()) {
            return;
        }

        CycleStartEvent startEvent = new CycleStartEvent(this.development.getPlugin(), target, 0);
        this.development.getPlugin().getEventBus().publish(startEvent);

        this.development.getPlugin().getGames().setNextRestart(false);

        Game game = this.development.getPlugin().getGames().getCurrentGame();
        if (game != null) {
            for (Countdown countdown : game.getRunningCountdowns()) {
                countdown.cancelCountdown();
            }

            sender.sendSuccess("Re-cycling now to " + target.getName() + "...");
            this.development.getPlugin().getGames().cycle(target);
        }
    }
}
