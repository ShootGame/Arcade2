package pl.themolka.arcade.window;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.game.GamePlayer;

public interface WindowListener {
    boolean onClick(GamePlayer player, ClickType click, int slot, ItemStack item);

    boolean onClose(GamePlayer player);

    void onCreate();

    boolean onOpen(GamePlayer player);
}
