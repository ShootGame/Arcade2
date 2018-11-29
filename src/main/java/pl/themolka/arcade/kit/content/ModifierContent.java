package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.attribute.Attribute;
import pl.themolka.arcade.attribute.BoundedModifier;
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

public class ModifierContent implements KitContent<BoundedModifier> {
    private final BoundedModifier result;

    protected ModifierContent(Config config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        Attribute attribute = player.getAttribute(this.result.getKey());
        if (attribute != null) {
            attribute.addModifier(this.result.getModifier());
        }
    }

    @Override
    public BoundedModifier getResult() {
        return this.result;
    }

    @NestedParserName({"modifier", "attribute-modifier", "attributemodifier", "attribute"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<BoundedModifier> modifierParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.modifierParser = context.type(BoundedModifier.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            BoundedModifier modifier = this.modifierParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<BoundedModifier> result() { return Ref.ofProvided(modifier); }
            });
        }
    }

    public interface Config extends KitContent.Config<ModifierContent, BoundedModifier> {
        @Override
        default ModifierContent create(Game game, Library library) {
            return new ModifierContent(this);
        }
    }
}
