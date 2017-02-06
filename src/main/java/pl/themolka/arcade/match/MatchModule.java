package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
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

@ModuleInfo(id = "Match")
public class MatchModule extends Module<MatchGame> {
    public static final int DEFAULT_START_COUNTDOWN = 15;
    public static final String METADATA_MATCH = "Match";
    public static final String METADATA_OBSERVERS = "Observers";

    @Override
    public MatchGame buildGameModule(Element xml, Game game) throws JDOMException {
        boolean autoCycle = true;
        boolean autoStart = true;
        int startCountdown = DEFAULT_START_COUNTDOWN;

        Element autoCycleElement = xml.getChild("auto-cycle");
        if (autoCycleElement != null) {
            autoCycle = XMLParser.parseBoolean(autoCycleElement.getTextNormalize(), true);
        }

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
                startCountdown = DEFAULT_START_COUNTDOWN;
            }
        }

        Observers observers = XMLObservers.parse(xml.getChild("observers"), this.getPlugin());
        if (observers.getColor() == null) {
            observers.setColor(ChatColor.AQUA);
        }
        if (observers.getName() == null) {
            observers.setName(Observers.OBSERVERS_NAME);
        }

        this.getGame().setMetadata(MatchModule.class, METADATA_OBSERVERS, observers);
        return new MatchGame(autoCycle, autoStart, startCountdown, observers);
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
        if (seconds < 3) {
            seconds = 3;
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
            permission = "arcade.command.gameinfo") // inherit from /gameinfo
    public void matchInfo(Sender sender, CommandContext context) {
        CommandUtils.sendTitleMessage(sender, "Match", "#" + this.getPlugin().getGames().getGameId());
        this.getPlugin().getEventBus().publish(new GameCommands.GameCommandEvent(this.getPlugin(), sender, context));
    }
}
