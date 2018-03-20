package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KitsGame extends GameModule {
    private final Map<String, Kit> kits = new LinkedHashMap<>();

    @Deprecated
    public KitsGame(List<Kit> kits) {
        for (Kit kit : kits) {
            this.addKit(kit);
        }
    }

    protected KitsGame(Game game, Config config) {
        for (Kit.Config kitConfig : config.kits()) {
            this.addKit(kitConfig.create(game));
        }
    }

    @Override
    public void onEnable() {
        this.insertContentFromInheritedKits();
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

    private void insertContentFromInheritedKits() {
        Collection<Kit> allKits = this.kits.values();
        for (Kit kit : allKits) {
            for (String inherit : kit.getInherit()) {
                for (Kit dependency : allKits) {
                    if (dependency.getId().equals(inherit)) {
                        kit.addContent(dependency);
                    }
                }
            }
        }
    }

    public interface Config extends IGameModuleConfig<KitsGame> {
        Set<Kit.Config> kits();

        @Override
        default KitsGame create(Game game) {
            return new KitsGame(game, this);
        }
    }
}
