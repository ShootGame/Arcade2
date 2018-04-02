package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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

public class ItemStackContent extends BaseInventoryContent<ItemStack>
                              implements BaseModeContent {
    // armor
    public static final int SLOT_HELMET = 103;
    public static final int SLOT_CHESTPLATE = 102;
    public static final int SLOT_LEGGNINGS = 101;
    public static final int SLOT_BOOTS = 100;

    private final Mode mode;
    private final Integer slot;

    protected ItemStackContent(Config config) {
        super(config);

        this.mode = config.mode();
        this.slot = config.slot();
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        if (this.give()) {
            if (this.hasSlot()) {
                inventory.setItem(this.getSlot(), this.getResult());
            } else {
                inventory.addItem(this.getResult());
            }
        } else if (this.take()) {
            inventory.remove(this.getResult());
        }
    }

    @Override
    public boolean give() {
        return this.mode.equals(Mode.GIVE);
    }

    @Override
    public boolean take() {
        return this.mode.equals(Mode.TAKE);
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean hasSlot() {
        return this.slot != null;
    }

    @NestedParserName({"item", "item-stack", "itemstack"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<ItemStack> itemStackParser;
        private Parser<Mode> modeParser;
        private Parser<Integer> slotParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.itemStackParser = context.type(ItemStack.class);
            this.modeParser = context.type(Mode.class);
            this.slotParser = context.type(Integer.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            ItemStack itemStack = this.itemStackParser.parseWithDefinition(node, name, value).orFail();
            BaseModeContent.Mode mode = this.modeParser.parseWithDefinition(node, name, value).orDefault(Config.DEFAULT_MODE);
            Integer slot = this.slotParser.parse(node.property("slot")).orDefault(Config.DEFAULT_SLOT);

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<ItemStack> result() { return Ref.ofProvided(itemStack); }
                public BaseModeContent.Mode mode() { return mode; }
                public Integer slot() { return slot; }
            });
        }
    }

    public interface Config extends BaseInventoryContent.Config<ItemStackContent, ItemStack>,
                                    BaseModeContent.Config {
        Integer DEFAULT_SLOT = null;

        default Integer slot() { return DEFAULT_SLOT; }

        @Override
        default ItemStackContent create(Game game) {
            return new ItemStackContent(this);
        }
    }
}
