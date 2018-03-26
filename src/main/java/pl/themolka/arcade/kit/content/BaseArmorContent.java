package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.game.GamePlayer;

public abstract class BaseArmorContent extends BaseInventoryContent<ItemStack>
                                       implements RemovableKitContent<ItemStack> {
    protected BaseArmorContent(Config<?> config) {
        super(config);
    }

    @Override
    public void apply(GamePlayer player, PlayerInventory inventory) {
        this.attach(player, this.getResult());
    }

    @Override
    public void attach(GamePlayer player, ItemStack value) {
        this.attach(player.getBukkit().getInventory(), value);
    }

    @Override
    public ItemStack defaultValue() {
        return Config.REMOVE;
    }

    public abstract void attach(PlayerInventory inventory, ItemStack value);

    public interface Config<T extends BaseArmorContent> extends BaseInventoryContent.Config<T, ItemStack>,
                                                                RemovableKitContent.Config<T, ItemStack> {
        ItemStack REMOVE = null;
    }
}
