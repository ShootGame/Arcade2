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

public class LevelContent implements RemovableKitContent<Integer> {
    private static final int MIN_VALUE = 0;

    public static boolean testValue(int value) {
        return value >= MIN_VALUE;
    }

    private final int result;

    protected LevelContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Integer value) {
        player.getBukkit().setLevel(value);
    }

    @Override
    public Integer defaultValue() {
        return Config.DEFAULT_LEVEL;
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    @NestedParserName({"level", "lvl"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> levelParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.levelParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Integer> result() { return Ref.empty(); }
                });
            }

            int level = this.levelParser.parse(node).orFail();
            if (level < 0) {
                throw this.fail(node, name, value, "Level must be positive (greater than 0)");
            }

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(level); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<LevelContent, Integer> {
        int DEFAULT_LEVEL = 0;

        @Override
        default LevelContent create(Game game) {
            return new LevelContent(this);
        }
    }
}
