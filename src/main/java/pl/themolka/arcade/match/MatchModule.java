package pl.themolka.arcade.match;

import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

import java.util.List;

@ModuleInfo(id = "Match")
public class MatchModule extends Module<MatchGame> {
    public static final int DEFAULT_START_COUNTDOWN = 15;

    @CommandInfo(name = {"begin", "start"},
            description = "Begin the match",
            flags = {"f", "force"},
            usage = "[-force] [seconds]",
            permission = "arcade.command.begin")
    public void begin(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        MatchGame game = this.getGameModule();
        game.handleBeginCommand(sender, context.getParamInt(0, -1), context.hasFlag("f") || context.hasFlag("force"));
    }

    @CommandInfo(name = {"end", "finish"},
            description = "End current match",
            flags = {"a", "auto",
                    "d", "draw"},
            usage = "[?|*|<winner...>]",
            permission = "arcade.command.end",
            completer = "endCompleter")
    public void end(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        boolean paramAuto = context.hasFlag("a") || context.hasFlag("auto");
        boolean paramDraw = context.hasFlag("d") || context.hasFlag("draw");

        String args = context.getParams(0);
        if (args != null) {
            if (args.equals("?")) {
                paramAuto = true;
            } else if (args.equals("*")) {
                paramDraw = true;
            }
        }

        MatchGame game = this.getGameModule();
        game.handleEndCommand(sender, paramAuto, args, paramDraw);
    }

    public List<String> endCompleter(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        MatchGame game = this.getGameModule();
        return game.handleEndCompleter(sender, context);
    }

    @CommandInfo(name = {"matchinfo", "match"},
            description = "Describe the match",
            permission = "arcade.command.gameinfo") // inherit from /gameinfo
    public void matchInfo(Sender sender, CommandContext context) {
        CommandUtils.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getCurrentGame().getGameId());
        this.getPlugin().getEventBus().publish(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }
}
