package pl.themolka.arcade.match;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.session.Session;

@ModuleInfo(id = "match")
public class MatchModule extends Module<MatchGame> {
    @Override
    public MatchGame buildGameModule(Element xml) throws JDOMException {
        return new MatchGame();
    }

    @Override
    public void onEnable(Element global) throws JDOMException {
        super.onEnable(global);
    }

    @CommandInfo(name = {"begin", "start"},
            description = "Begin the match",
            flags = {"f", "force"},
            usage = "[-force] [seconds]",
            permission = "arcade.command.begin")
    public void begin(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        boolean paramForce = context.hasFlag("f") || context.hasFlag("force");
        int paramSeconds = context.getParamInt(0);

        int seconds = paramSeconds;
        if (seconds < 5) {
            seconds = 5;
        }

        this.getGameModule().handleBeginCommand(sender, seconds, paramForce);
    }

    @CommandInfo(name = {"end", "finish"},
            description = "End current match",
            flags = {"d", "draw"},
            usage = "[-draw] [<winner...>]",
            permission = "arcade.command.end",
            completer = "endCompleter")
    public void end(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        boolean paramDraw = context.hasFlag("d") || context.hasFlag("draw");
        String paramWinner = context.getParams(0);

        this.getGameModule().handleEndCommand(sender, paramWinner, paramDraw);
    }

    @CommandInfo(name = {"matchinfo", "match"},
            description = "Describes the match",
            permission = "arcade.command.matchinfo")
    public void matchInfo(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        Commands.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getGameId());
        this.getPlugin().getEvents().post(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }
}
