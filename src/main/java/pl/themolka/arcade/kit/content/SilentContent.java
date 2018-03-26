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

public class SilentContent implements RemovableKitContent<Boolean> {
    private final boolean result;

    protected SilentContent(Config config) {
        this.result = config.result().getOrDefault(this.defaultValue());
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Boolean value) {
        player.getBukkit().setSilent(value);
    }

    @Override
    public Boolean defaultValue() {
        return Config.DEFAULT_SILENT;
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName("silent")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> silentParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.silentParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<Boolean> result() { return Ref.empty(); }
                });
            }

            boolean silent = this.silentParser.parse(node).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(silent); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<SilentContent, Boolean> {
        boolean DEFAULT_SILENT = false;

        @Override
        default SilentContent create(Game game) {
            return new SilentContent(this);
        }
    }
}
