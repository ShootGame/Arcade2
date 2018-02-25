package pl.themolka.arcade.map;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.generator.Generator;

public class WorldInfoBuilder implements Builder<WorldInfo> {
    private Difficulty difficulty;
    private World.Environment environment;
    private Generator generator;
    private boolean pvp;
    private long randomSeed;
    private Location spawn;
    private MapTime time;
    private World world;
    private String worldName;

    @Override
    public WorldInfo build() {
        WorldInfo info = new WorldInfo();
        info.setDifficulty(this.difficulty);
        return info;
    }

    public Difficulty difficulty() {
        return this.difficulty;
    }

    public WorldInfoBuilder difficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public World.Environment environment() {
        return this.environment;
    }

    public WorldInfoBuilder environment(World.Environment environment) {
        this.environment = environment;
        return this;
    }

    public Generator generator() {
        return this.generator;
    }

    public WorldInfoBuilder generator(Generator generator) {
        this.generator = generator;
        return this;
    }

    public boolean pvp() {
        return this.pvp;
    }

    public WorldInfoBuilder pvp(boolean pvp) {
        this.pvp = pvp;
        return this;
    }

    public long randomSeed() {
        return this.randomSeed;
    }

    public WorldInfoBuilder randomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
        return this;
    }

    public Location spawn() {
        return this.spawn;
    }

    public WorldInfoBuilder spawn(Location spawn) {
        this.spawn = spawn;
        return this;
    }

    public MapTime time() {
        return this.time;
    }

    public WorldInfoBuilder time(MapTime time) {
        this.time = time;
        return this;
    }

    public World world() {
        return this.world;
    }

    public WorldInfoBuilder world(World world) {
        this.world = world;
        return this;
    }

    public String worldName() {
        return this.worldName;
    }

    public WorldInfoBuilder worldName(String worldName) {
        this.worldName = worldName;
        return this;
    }
}
