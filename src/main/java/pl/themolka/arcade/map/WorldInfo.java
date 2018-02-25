package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.generator.Generator;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class WorldInfo {
    private Difficulty difficulty;
    private World.Environment environment;
    private Generator generator;
    private boolean pvp;
    private long randomSeed;
    private Location spawn;
    private MapTime time;
    private Reference<World> world;
    private String worldName;

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public World.Environment getEnvironment() {
        return this.environment;
    }

    public Generator getGenerator() {
        return this.generator;
    }

    public long getRandomSeed() {
        return this.randomSeed;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public MapTime getTime() {
        return this.time;
    }

    public World getWorld() {
        return this.world.get();
    }

    public String getWorldName() {
        return this.worldName;
    }

    public boolean isPvp() {
        return this.pvp;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setTime(MapTime time) {
        this.time = time;
    }

    public void setWorld(World world) {
        this.world = new WeakReference<>(world);
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
