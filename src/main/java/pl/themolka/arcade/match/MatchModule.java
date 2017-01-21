package pl.themolka.arcade.match;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.time.XMLTime;
import pl.themolka.arcade.xml.XMLParser;

import java.util.List;

@ModuleInfo(id = "match")
public class MatchModule extends Module<MatchGame> {
    public static final int DEFAULT_START_COUNTDOWN = 25;
    public static final String METADATA_MATCH = "Match";
    public static final String METADATA_OBSERVERS = "Observers";

    private Observers defaultObservers;
    private int defaultStartCountdown = DEFAULT_START_COUNTDOWN;

    @Override
    public MatchGame buildGameModule(Element xml, Game game) throws JDOMException {
        boolean autoStart = true;
        int startCountdown = this.defaultStartCountdown;

        Element autoStartElement = xml.getChild("auto-start");
        if (autoStartElement != null) {
            autoStart = XMLParser.parseBoolean(autoStartElement.getTextNormalize(), true);
        }

        Element startCountdownElement = xml.getChild("start-countdown");
        if (startCountdownElement != null) {
            Time time = XMLTime.parse(startCountdownElement.getTextNormalize());
            if (time != null) {
                startCountdown = (int) time.toSeconds();
            } else {
                startCountdown = this.getDefaultStartCountdown();
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

        this.getGame().setMetadata(MatchModule.class, METADATA_OBSERVERS, observers);
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
                this.defaultStartCountdown = DEFAULT_START_COUNTDOWN;
            }
        }
    }

    @CommandInfo(name = {"begin", "start"},
            description = "Begin the match",
            flags = {"f", "force"},
            usage = "[-force] [seconds]",
            permission = "arcade.command.begin")
    public void begin(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        int seconds = context.getParamInt(0);
        if (seconds < 5) {
            seconds = 5;
        }

        MatchGame game = this.getGameModule();
        game.handleBeginCommand(sender, seconds, context.hasFlag("f") || context.hasFlag("force"));
    }

    @CommandInfo(name = {"end", "finish"},
            description = "End current match",
            flags = {"a", "auto",
                    "d", "draw"},
            usage = "[-auto|-draw|<winner...>]",
            permission = "arcade.command.end",
            completer = "endCompleter")
    public void end(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        boolean paramAuto = context.hasFlag("a") || context.hasFlag("auto");
        boolean paramDraw = context.hasFlag("d") || context.hasFlag("draw");

        MatchGame game = this.getGameModule();
        game.handleEndCommand(sender, paramAuto, context.getParams(0), paramDraw);
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
            permission = "arcade.command.matchinfo")
    public void matchInfo(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Match module is not enabled in this game.");
        }

        CommandUtils.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getGameId());
        this.getPlugin().getEventBus().publish(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }

    public Observers getDefaultObservers() {
        return this.defaultObservers;
    }

    public int getDefaultStartCountdown() {
        return this.defaultStartCountdown;
    }
}
