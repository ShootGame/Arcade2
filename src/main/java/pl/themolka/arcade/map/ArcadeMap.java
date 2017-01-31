package pl.themolka.arcade.map;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.generator.GeneratorType;
import pl.themolka.arcade.scoreboard.ScoreboardContext;

public class ArcadeMap {
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.PEACEFUL;
    public static final World.Environment DEFAULT_ENVIRONMENT = World.Environment.NORMAL;
    public static final Generator DEFAULT_GENERATOR = GeneratorType.getDefaultGenerator();
    public static final long DEFAULT_SEED = 0L;

    private final OfflineMap mapInfo;

    private ArcadeMapConfiguration configuration;
    private Difficulty difficulty;
    private World.Environment environment;
    private transient Game game;
    private transient Generator generator;
    private boolean pvp;
    private String scoreboardTitle;
    private long seed;
    private transient Location spawn;
    private transient World world;
    private String worldName;

    public ArcadeMap(OfflineMap mapInfo) {
        this.mapInfo = mapInfo;
    }

    public OfflineMap getMapInfo() {
        return this.mapInfo;
    }

    public ArcadeMapConfiguration getConfiguration() {
        return this.configuration;
    }

    public Difficulty getDifficulty() {
        if (this.hasDifficulty()) {
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

    public Game getGame() {
        return this.game;
    }

    public Generator getGenerator() {
        if (this.hasGenerator()) {
            return this.generator;
        }

        return DEFAULT_GENERATOR;
    }

    public String getScoreboardTitle() {
        if (this.hasScoreboardTitle()) {
            return this.scoreboardTitle;
        }

        String mapName = ChatColor.YELLOW + this.getMapInfo().getName();
        if (mapName.length() <= ScoreboardContext.OBJECTIVE_MAX_LENGTH) {
            return mapName;
        }

        return null;
    }

    public long getSeed() {
        if (this.hasSeed()) {
            return this.seed;
        }

        return DEFAULT_SEED;
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

    public boolean hasGenerator() {
        return this.generator != null;
    }

    public boolean hasScoreboardTitle() {
        return this.scoreboardTitle != null;
    }

    public boolean hasSeed() {
        return this.seed != DEFAULT_SEED;
    }

    public boolean isPvp() {
        return this.pvp;
    }

    public void setConfiguration(ArcadeMapConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setEnvironment(World.Environment environment) {
        this.environment = environment;
    }

    public void setGame(Game game) {
        if (this.game == null) {
            this.game = game;
        }
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public void setScoreboardTitle(String title) {
        if (title == null || title.length() <= ScoreboardContext.OBJECTIVE_MAX_LENGTH) {
            this.scoreboardTitle = title;
        }
    }

    public void setSeed(long seed) {
        this.seed = seed;
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
