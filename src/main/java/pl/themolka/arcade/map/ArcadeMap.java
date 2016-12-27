package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.jdom2.Element;

public class ArcadeMap {
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.PEACEFUL;
    public static final World.Environment DEFAULT_ENVIRONMENT = World.Environment.NORMAL;

    private final OfflineMap mapInfo;

    private Element configuration;
    private Difficulty difficulty;
    private World.Environment environment;
    private boolean pvp;
    private Location spawn;
    private World world;
    private String worldName;

    public ArcadeMap(OfflineMap mapInfo) {
        this.mapInfo = mapInfo;
    }

    public OfflineMap getMapInfo() {
        return this.mapInfo;
    }

    public Element getConfiguration() {
        return this.configuration;
    }

    public Difficulty getDifficulty() {
        if (this.hasEnvironment()) {
            return this.difficulty;
        }

        return DEFAULT_DIFFICULTY;
    }

    public World.Environment getEnvironment() {
        if (this.hasEnvironment()) {
            return this.environment;
        }

        return DEFAULT_ENVIRONMENT;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public World getWorld() {
        return this.world;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public boolean hasDifficulty() {
        return this.difficulty != null;
    }

    public boolean hasEnvironment() {
        return this.environment != null;
    }

    public boolean isPvp() {
        return this.pvp;
    }

    public void setConfiguration(Element configuration) {
        this.configuration = configuration;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
