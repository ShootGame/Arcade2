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

public class LevelContent implements RemovableKitContent<PlayerLevel> {
    private static final int MIN_VALUE = 0;

    public static boolean testValue(int value) {
        return value >= MIN_VALUE;
    }

    private final PlayerLevel result;

    protected LevelContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void attach(GamePlayer player, PlayerLevel value) {
        value.apply(player);
    }

    @Override
    public PlayerLevel defaultValue() {
        return Config.DEFAULT_LEVEL;
    }

    @Override
    public PlayerLevel getResult() {
        return this.result;
    }

    @NestedParserName({"level", "lvl"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<PlayerLevel> levelParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.levelParser = context.type(PlayerLevel.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<PlayerLevel> result() { return Ref.empty(); }
                });
            }

            PlayerLevel level = this.levelParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<PlayerLevel> result() { return Ref.ofProvided(level); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<LevelContent, PlayerLevel> {
        PlayerLevel DEFAULT_LEVEL = PlayerLevel.getDefaultLevel();

        @Override
        default LevelContent create(Game game, Library library) {
            return new LevelContent(this);
        }
    }
}