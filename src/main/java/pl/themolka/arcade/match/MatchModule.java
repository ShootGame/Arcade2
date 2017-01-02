package pl.themolka.arcade.match;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.Commands;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.game.Game;
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

    private Observers defaultObservers;
    private int defaultStartCountdown = DEFAULT_START_COUNTDOWN;

    @Override
    public MatchGame buildGameModule(Element xml, Game game) throws JDOMException {
        boolean autoStart = true;
        int startCountdown = this.defaultStartCountdown;

        Element autoStartElement = xml.getChild("auto-start");
        if (autoStartElement != null) {
            autoStart = autoStartElement.getTextNormalize().equalsIgnoreCase("true");
        }

        Element startCountdownElement = xml.getChild("start-countdown");
        if (startCountdownElement != null) {
            try {
                startCountdown = Integer.parseInt(startCountdownElement.getTextNormalize());
            } catch (NumberFormatException ignored) {
            }
        }

        Observers observers = XMLObservers.parse(xml.getChild("observers"), this.getPlugin());
        if (observers.getColor() == null) {
            observers.setColor(this.getDefaultObservers().getColor());
        }
        if (observers.getDyeColor() == null) {
            observers.setDyeColor(this.getDefaultObservers().getDyeColor());
        }
        if (observers.getName() == null) {
            observers.setName(this.getDefaultObservers().getName());
        }

        return new MatchGame(autoStart, startCountdown, observers);
    }

    @Override
    public void onEnable(Element global) throws JDOMException {
        this.onEnableObservers(global.getChild("observers"));
        this.onEnableStartCountdown(global.getChild("start-countdown"));
    }

    private void onEnableObservers(Element xml) throws JDOMException {
        Observers observers = XMLObservers.parse(xml, this.getPlugin());
        if (observers.getColor() == null) {
            observers.setColor(Observers.OBSERVERS_COLOR);
        }
        if (observers.getDyeColor() == null) {
            observers.setDyeColor(Observers.OBSERVERS_DYE_COLOR);
        }
        if (observers.getName() == null) {
            observers.setName(Observers.OBSERVERS_NAME);
        }

        this.defaultObservers = observers;
    }

    private void onEnableStartCountdown(Element xml) throws JDOMException {
        if (xml != null) {
            try {
                this.defaultStartCountdown = Integer.parseInt(xml.getTextNormalize());
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

        int seconds = context.getParamInt(0);
        if (seconds < 5) {
            seconds = 5;
        }

        MatchGame game = (MatchGame) this.getGameModule();
        game.handleBeginCommand(sender, seconds, context.hasFlag("f") || context.hasFlag("force"));
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

        MatchGame game = (MatchGame) this.getGameModule();
        game.handleEndCommand(sender, paramAuto, context.getParam(0), paramDraw);
    }

    public List<String> endCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        MatchGame game = (MatchGame) this.getGameModule();
        return game.handleEndCompleter(sender, context);
    }

    @CommandInfo(name = {"matchinfo", "match"},
            description = "Describe the match",
            permission = "arcade.command.matchinfo")
    public void matchInfo(Session<ArcadePlayer> sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        Commands.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getGameId());
        this.getPlugin().getEventBus().publish(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }

    public Observers getDefaultObservers() {
        return this.defaultObservers;
    }

    public int getDefaultStartCountdown() {
        return this.defaultStartCountdown;
    }
}
