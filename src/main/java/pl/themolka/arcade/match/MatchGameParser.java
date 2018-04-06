package pl.themolka.arcade.match;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.time.Time;

@Produces(MatchGame.Config.class)
public class MatchGameParser extends GameModuleParser<MatchGame, MatchGame.Config>
                             implements InstallableParser {
    private Parser<Boolean> autoCycleParser;
    private Parser<Boolean> autoStartParser;
    private Parser<Time> startCountdownParser;
    private Parser<Observers.Config> observersParser;

    public MatchGameParser() {
        super(MatchGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("match");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.autoCycleParser = context.type(Boolean.class);
        this.autoStartParser = context.type(Boolean.class);
        this.startCountdownParser = context.type(Time.class);
        this.observersParser = context.type(Observers.Config.class);
    }

    @Override
    protected ParserResult<MatchGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        boolean autoCycle = this.autoCycleParser.parse(node.property("auto-cycle", "autocycle")).orDefault(MatchGame.Config.DEFAULT_IS_AUTO_CYCLE);
        boolean autoStart = this.autoStartParser.parse(node.property("auto-start", "autostart")).orDefault(MatchGame.Config.DEFAULT_IS_AUTO_START);
        Time startCountdown = this.startCountdownParser.parse(node.property("start-countdown", "startcountdown")).orDefault(MatchGame.Config.DEFAULT_START_COUNTDOWN);
        Observers.Config observers = this.observersParser.parse(node.firstChild("observers")).orFail();

        return ParserResult.fine(node, name, value, new MatchGame.Config() {
            public boolean autoCycle() { return autoCycle; }
            public boolean autoStart() { return autoStart; }
            public Time startCountdown() { return startCountdown; }
            public Ref<Observers.Config> observers() { return Ref.ofProvided(observers); }
        });
    }
}
