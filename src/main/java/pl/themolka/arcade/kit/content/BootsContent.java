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
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

public class BootsContent extends BaseArmorContent {
    public BootsContent(Config config) {
        super(config);
    }

    @Override
    public void attach(PlayerInventory inventory, ItemStack value) {
        inventory.setBoots(value);
    }

    @NestedParserName({"boots", "armor-boots", "armorboots"})
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
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            if (this.reset(node)) {
                return Result.fine(node, name, value, new Config() {
                    public Ref<ItemStack> result() { return Ref.empty(); }
                });
            }

            ItemStack boots = this.itemStackParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<ItemStack> result() { return Ref.ofProvided(boots); }
            });
        }
    }

    public interface Config extends BaseArmorContent.Config<BootsContent> {
        @Override
        default BootsContent create(Game game, Library library) {
            return new BootsContent(this);
        }
    }
}
