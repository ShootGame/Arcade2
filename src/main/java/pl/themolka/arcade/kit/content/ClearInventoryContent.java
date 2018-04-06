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

public class ClearInventoryContent implements KitContent<Boolean> {
    private final boolean result;

    protected ClearInventoryContent(Config config) {
        this.result = config.result().getOrDefault(false);
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().clearInventory(this.result);
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    @NestedParserName({"clear-inventory", "clearinventory", "clear"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> armorParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.armorParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            boolean armor = this.armorParser.parse(node.property("armor", "full")).orDefault(false);

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Boolean> result() { return Ref.ofProvided(armor); }
            });
        }
    }

    public interface Config extends KitContent.Config<ClearInventoryContent, Boolean> {
        @Override
        default ClearInventoryContent create(Game game, Library library) {
            return new ClearInventoryContent(this);
        }
    }
}
