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

public class GiveLevelContent implements KitContent<Integer> {
    public static boolean testValue(int value) {
        return value != 0;
    }

    private final int result;

    protected GiveLevelContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().giveExpLevels(this.result);
    }

    @Override
    public Integer getResult() {
        return this.result;
    }

    @NestedParserName({"give-level", "givelevel", "give-levels", "givelevels",
                       "take-level", "takelevel", "take-levels", "takelevels"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Integer> levelParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.levelParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            int level = this.levelParser.parse(node).orFail();
            if (level <= 0) {
                throw this.fail(node, name, value, "Level must be positive (greater than 0)");
            }

            if (name.toLowerCase().startsWith("take")) {
                level = -level;
            }

            final int finalLevel = level;
            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Integer> result() { return Ref.ofProvided(finalLevel); }
            });
        }
    }

    public interface Config extends KitContent.Config<GiveLevelContent, Integer> {
        @Override
        default GiveLevelContent create(Game game) {
            return new GiveLevelContent(this);
        }
    }
}
