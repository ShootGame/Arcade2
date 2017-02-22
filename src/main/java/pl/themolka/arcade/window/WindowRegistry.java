package pl.themolka.arcade.window;

import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WindowRegistry {
    private final Map<Inventory, Window> windows = new HashMap<>();

    public void addWindow(Window window) {
        this.windows.put(window.getContainer(), window);
    }

    public Window getWindow(Inventory view) {
        return this.windows.get(view);
    }

    public Collection<Window> getWindows() {
        return this.windows.values();
    }

    public boolean hasView(Inventory view) {
        return this.windows.containsKey(view);
    }

    public void removeAll() {
        this.windows.clear();
    }

    public void removeWindow(Inventory view) {
        this.windows.remove(view);
    }

    public void removeWindow(Window window) {
        this.removeWindow(window.getContainer());
    }
}
