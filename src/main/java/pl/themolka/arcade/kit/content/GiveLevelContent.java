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
import pl.themolka.arcade.session.PlayerLevel;

public class GiveLevelContent implements KitContent<PlayerLevel> {
    public static boolean testValue(int value) {
        return value != 0;
    }

    private final PlayerLevel result;

    protected GiveLevelContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        this.getResult().applyIncremental(player);
    }

    @Override
    public PlayerLevel getResult() {
        return this.result;
    }

    @NestedParserName({"give-level", "givelevel", "give-levels", "givelevels", "give-lvl", "givelvl",
                       "take-level", "takelevel", "take-levels", "takelevels", "take-lvl", "takelvl"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<PlayerLevel> levelParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.levelParser = context.type(PlayerLevel.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            PlayerLevel level = this.levelParser.parseWithDefinition(node, name, value).orFail();

            if (name.toLowerCase().startsWith("take")) {
                level = level.negate();
            }

            final PlayerLevel finalLevel = level;
            return ParserResult.fine(node, name, value, new Config() {
                public Ref<PlayerLevel> result() { return Ref.ofProvided(finalLevel); }
            });
        }
    }

    public interface Config extends KitContent.Config<GiveLevelContent, PlayerLevel> {
        @Override
        default GiveLevelContent create(Game game) {
            return new GiveLevelContent(this);
        }
    }
}
