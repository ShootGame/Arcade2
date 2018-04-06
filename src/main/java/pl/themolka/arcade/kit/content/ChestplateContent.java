package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class ChestplateContent extends BaseArmorContent {
    public ChestplateContent(Config config) {
        super(config);
    }

    @Override
    public void attach(PlayerInventory inventory, ItemStack value) {
        inventory.setChestplate(value);
    }

    @NestedParserName({"chestplate", "armor-chestplate", "armorchestplate"})
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<ItemStack> itemStackParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.itemStackParser = context.type(ItemStack.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return ParserResult.fine(node, name, value, new Config() {
                    public Ref<ItemStack> result() { return Ref.empty(); }
                });
            }

            ItemStack chestplate = this.itemStackParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<ItemStack> result() { return Ref.ofProvided(chestplate); }
            });
        }
    }

    public interface Config extends BaseArmorContent.Config<ChestplateContent> {
        @Override
        default ChestplateContent create(Game game, Library library) {
            return new ChestplateContent(this);
        }
    }
}
