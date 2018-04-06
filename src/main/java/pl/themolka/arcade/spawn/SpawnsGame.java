package pl.themolka.arcade.spawn;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpawnsGame extends GameModule {
    private final Map<String, Spawn> spawns = new HashMap<>();

    protected SpawnsGame(Game game, IGameConfig.Library library, Config config) {
        for (Spawn.Config<?> spawn : config.spawns().get()) {
            String id = spawn.id();
            if (StringUtils.isNotEmpty(id)) {
                this.spawns.put(id, library.getOrDefine(game, spawn));
            }
        }
    }

    public void addSpawn(String id, Spawn spawn) {
        this.spawns.put(id, spawn);
    }

    public Spawn getSpawn(String id) {
        return this.getSpawn(id, null);
    }

    public Spawn getSpawn(String id, Spawn def) {
        return id != null ? this.spawns.getOrDefault(id.trim(), def) : def;
    }

    public Set<String> getSpawnIds() {
        return this.spawns.keySet();
    }

    public List<Spawn> getSpawns() {
        return new ArrayList<>(this.spawns.values());
    }

    public boolean removeSpawn(String id) {
        return this.spawns.remove(id) != null;
    }

    public interface Config extends IGameModuleConfig<SpawnsGame> {
        Ref<List<Spawn.Config<?>>> spawns();

        @Override
        default SpawnsGame create(Game game, Library library) {
            return new SpawnsGame(game, library, this);
        }
    }
}
