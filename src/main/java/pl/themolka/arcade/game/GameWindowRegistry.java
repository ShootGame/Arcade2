package pl.themolka.arcade.game;

import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.window.Window;
import pl.themolka.arcade.window.WindowRegistry;

public class GameWindowRegistry extends WindowRegistry {
    private final Game game;

    public GameWindowRegistry(Game game) {
        this.game = game;
    }

    @Override
    public void addWindow(Window window) {
        super.addWindow(window);
        this.getParent().addWindow(window);
    }

    @Override
    public void removeAll() {
        for (Window window : this.getWindows()) {
            this.getParent().removeWindow(window);
        }

        super.removeAll();
    }

    @Override
    public void removeWindow(Inventory view) {
        this.getParent().removeWindow(view);
        super.removeWindow(view);
    }

    private WindowRegistry getParent() {
        return this.game.getPlugin().getWindowRegistry();
    }
}
