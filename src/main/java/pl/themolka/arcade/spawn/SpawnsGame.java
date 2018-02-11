package pl.themolka.arcade.spawn;

import pl.themolka.arcade.game.GameModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpawnsGame extends GameModule {
    private final Map<String, Spawn> spawns;

    public SpawnsGame(Map<String, Spawn> spawns) {
        this.spawns = spawns;
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
}
