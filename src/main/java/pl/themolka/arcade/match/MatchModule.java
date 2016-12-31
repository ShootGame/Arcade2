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

import java.util.List;

@ModuleInfo(id = "match")
public class MatchModule extends Module<MatchGame> {
    public static final int DEFAULT_START_COUNTDOWN = 25;
    public static final String METADATA_MATCH = "match";

    private int startCountdown = DEFAULT_START_COUNTDOWN;

    @Override
    public MatchGame buildGameModule(Element xml) throws JDOMException {
        boolean autoStart = true;
        int defaultStartCountdown = this.startCountdown;

        Element autoStartElement = xml.getChild("auto-start");
        if (autoStartElement != null) {
            autoStart = autoStartElement.getTextNormalize().equalsIgnoreCase("true");
        }

        Element startCountdownElement = xml.getChild("start-countdown");
        if (startCountdownElement != null) {
            try {
                defaultStartCountdown = Integer.parseInt(startCountdownElement.getTextNormalize());
            } catch (NumberFormatException ignored) {
            }
        }

        return new MatchGame(autoStart, defaultStartCountdown);
    }

    @Override
    public void onEnable(Element global) throws JDOMException {
        Element startCountdownElement = global.getChild("start-countdown");
        if (startCountdownElement != null) {
            try {
                this.startCountdown = Integer.parseInt(startCountdownElement.getTextNormalize());
            } catch (NumberFormatException ignored) {
            }
        }
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
            flags = {"a", "auto",
                    "d", "draw"},
            usage = "[-auto|-draw|<winner...>]",
            permission = "arcade.command.end",
            completer = "endCompleter")
    public void end(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        boolean paramAuto = context.hasFlag("a") || context.hasFlag("auto");
        boolean paramDraw = context.hasFlag("d") || context.hasFlag("draw");
        String paramWinner = context.getParams(0);

        this.getGameModule().handleEndCommand(sender, paramAuto, paramWinner, paramDraw);
    }

    public List<String> endCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        return this.getGameModule().handleEndCompleter(sender, context);
    }

    @CommandInfo(name = {"matchinfo", "match"},
            description = "Describe the match",
            permission = "arcade.command.matchinfo")
    public void matchInfo(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        Commands.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getGameId());
        this.getPlugin().getEvents().post(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }
}
