package pl.themolka.arcade.map;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameHolder;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.generator.GeneratorType;
import pl.themolka.arcade.scoreboard.ScoreboardContext;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ArcadeMap implements GameHolder {
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.PEACEFUL;
    public static final World.Environment DEFAULT_ENVIRONMENT = World.Environment.NORMAL;
    public static final Generator DEFAULT_GENERATOR = GeneratorType.getDefaultGenerator();
    public static final long DEFAULT_SEED = 0L;
    public static final MapTime DEFAULT_TIME = MapTime.defaultTime();

    private final OfflineMap mapInfo; // TODO: mutable?

    @Deprecated private ArcadeMapConfiguration configuration; // bye bye
    @Deprecated private Difficulty difficulty; // moved to WorldInfo
    @Deprecated private World.Environment environment; // moved to WorldInfo
    private Game game;
    @Deprecated private Generator generator; // moved to WorldInfo
    @Deprecated private boolean pvp; // moved to WorldInfo
    @Deprecated private String scoreboardTitle; // moved to ScoreboardInfo
    @Deprecated private long seed; // moved to WorldInfo
    @Deprecated private Location spawn; // moved to WorldInfo
    @Deprecated private MapTime time; // moved to WorldInfo
    private Reference<World> world;
    private String worldName;

    // ---------------------------  New Fields  --------------------------- //
    // These fields will replace @deprecated fields. Don't forger to finish //
    //  MapManifestParser - it puts null in the constructor of this class.  //
    // -------------------------------------------------------------------- //
    private final WorldInfo worldInfo = null; // init in constructor
    private final ScoreboardInfo scoreboardInfo = null; // init in constructor
    private final ModulesInfo modulesInfo = null; // init in constructor

    public ArcadeMap(OfflineMap mapInfo) {
        this.mapInfo = mapInfo;
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public ArcadeMap getMap() {
        return this;
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

    public MapTime getTime() {
        if (this.hasTime()) {
            return this.time;
        }

        return DEFAULT_TIME;
    }

    public World getWorld() {
        return this.world.get();
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

    public boolean hasTime() {
        return this.time != null;
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
