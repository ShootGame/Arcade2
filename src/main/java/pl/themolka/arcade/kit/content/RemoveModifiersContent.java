package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.attribute.Attribute;
import pl.themolka.arcade.attribute.AttributeKey;
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
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class RemoveModifiersContent implements KitContent<AttributeKey> {
    private final AttributeKey result;

    protected RemoveModifiersContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        Attribute attribute = player.getAttribute(this.result);
        if (attribute != null) {
            attribute.removeAllModifers();
        }
    }

    @Override
    public AttributeKey getResult() {
        return this.result;
    }

    @NestedParserName({"remove-modifers", "removemodifers"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<AttributeKey> keyParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.keyParser = context.type(AttributeKey.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            AttributeKey key = this.keyParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<AttributeKey> result() { return Ref.ofProvided(key); }
            });
        }
    }

    public interface Config extends KitContent.Config<RemoveModifiersContent, AttributeKey> {
        @Override
        default RemoveModifiersContent create(Game game, Library library) {
            return new RemoveModifiersContent(this);
        }
    }
}
