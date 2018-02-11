package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.GameModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitsGame extends GameModule {
    private final Map<String, Kit> kits = new HashMap<>();

    public KitsGame(List<Kit> kits) {
        for (Kit kit : kits) {
            this.addKit(kit);
        }
    }

    public void addKit(Kit kit) {
        this.kits.put(kit.getId(), kit);
    }

    public Kit getKit(String id) {
        return this.getKit(id, null);
    }

    public Kit getKit(String id, Kit def) {
        return id != null ? this.kits.getOrDefault(id.trim(), def) : def;
    }

    public Set<String> getKitIds() {
        return this.kits.keySet();
    }

    public Collection<Kit> getKits() {
        return this.kits.values();
    }

    public void removeKit(Kit kit) {
        this.removeKit(kit.getId());
    }

    public void removeKit(String id) {
        this.kits.remove(id);
    }
}
