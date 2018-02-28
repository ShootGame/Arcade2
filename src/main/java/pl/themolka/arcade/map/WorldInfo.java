package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.generator.GeneratorType;

public class WorldInfo implements Cloneable {
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.EASY;
    public static final World.Environment DEFAULT_ENVIRONMENT = World.Environment.NORMAL;
    public static final Generator DEFAULT_GENERATOR = GeneratorType.getDefaultGenerator();
    public static final boolean DEFAULT_IS_PVP = true;
    public static final RandomSeed DEFAULT_RANDOM_SEED = new RandomSeed(RandomSeed.DEFAULT_SEED);
    public static final Location DEFAULT_SPAWN = new Location((World) null, 0.5D, 16D, 0.5D);
    public static final MapTime DEFAULT_TIME = MapTime.defaultTime();

    private Difficulty difficulty;
    private World.Environment environment;
    private Generator generator;
    private boolean pvp = DEFAULT_IS_PVP;
    private RandomSeed randomSeed;
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
        return this.difficulty != null ? this.difficulty : DEFAULT_DIFFICULTY;
    }

    public World.Environment getEnvironment() {
        return this.environment != null ? this.environment : DEFAULT_ENVIRONMENT;
    }

    public Generator getGenerator() {
        return this.generator != null ? this.generator : DEFAULT_GENERATOR;
    }

    public RandomSeed getRandomSeed() {
        return this.randomSeed != null ? this.randomSeed : DEFAULT_RANDOM_SEED;
    }

    public Location getSpawn() {
        return this.spawn != null ? this.spawn : DEFAULT_SPAWN;
    }

    public MapTime getTime() {
        return this.time != null ? this.time : DEFAULT_TIME;
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

    public void setRandomSeed(RandomSeed randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setTime(MapTime time) {
        this.time = time;
    }
}