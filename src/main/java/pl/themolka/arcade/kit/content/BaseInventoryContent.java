package pl.themolka.arcade.kit.content;

import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.game.GamePlayer;

public abstract class BaseInventoryContent<T> implements KitContent<T> {
    private final T result;

    protected BaseInventoryContent(Config<?, T> config) {
        this.result = config.result().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player) && !player.isDead();
    }

    @Override
    public void apply(GamePlayer player) {
        this.apply(player, player.getBukkit().getInventory());
    }

    @Override
    public T getResult() {
        return this.result;
    }

    public abstract void apply(GamePlayer player, PlayerInventory inventory);

    public interface Config<T extends BaseInventoryContent<?>, R> extends KitContent.Config<T, R> {
    }
}
