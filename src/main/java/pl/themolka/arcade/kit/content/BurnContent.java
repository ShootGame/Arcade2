package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.time.TimeUtils;

public class BurnContent implements RemovableKitContent<Time> {
    private final Time result;

    protected BurnContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Time value) {
        player.getBukkit().setFireTicks(TimeUtils.toTicksInt(value));
    }

    @Override
    public Time defaultValue() {
        return Config.DEFAULT_TIME;
    }

    @Override
    public Time getResult() {
        return this.result;
    }

    @NestedParserName("burn")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Time> timeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.timeParser = context.type(Time.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Time> result() { return Ref.empty(); }
                });
            }

            Time time = this.timeParser.parseWithDefinition(node, name, value).orFail();
            if (time.isNegative()) {
                throw this.fail(node, name, value, "Burn time cannot be negative");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Time> result() { return Ref.ofProvided(time); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<BurnContent, Time> {
        Time DEFAULT_TIME = Time.ZERO;

        @Override
        default BurnContent create(Game game, Library library) {
            return new BurnContent(this);
        }
    }
}
