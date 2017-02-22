package pl.themolka.arcade.window;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.game.GamePlayer;

public class SimpleWindowListener implements WindowListener {
    @Override
    public boolean onClick(GamePlayer player, ClickType click, int slot, ItemStack item) {
        return false;
    }

    @Override
    public boolean onClose(GamePlayer player) {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public boolean onOpen(GamePlayer player) {
        return true;
    }
}
