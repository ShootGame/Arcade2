package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.generator.Generator;

public class WorldInfo implements Cloneable {
    private Difficulty difficulty;
    private World.Environment environment;
    private Generator generator;
    private boolean pvp;
    private long randomSeed;
    private Location spawn;
    private MapTime time;

    @Override
    public WorldInfo clone() {
        try {
            WorldInfo clone = (WorldInfo) super.clone();
            clone.difficulty = this.difficulty;
            clone.environment = this.environment;
            clone.generator = this.generator;
            clone.pvp = this.pvp;
            clone.randomSeed = this.randomSeed;
            clone.spawn = this.spawn.clone();
            clone.time = this.time.clone();
            return clone;
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

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
}
