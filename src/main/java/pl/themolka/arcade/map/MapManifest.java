package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Precise and playable map description. This class doesn't hold an
 * {@link OfflineMap}.
 */
public class MapManifest {
    public static final String FILENAME = "map.xml";

    private final Set<IGameModuleConfig<?>> modules;
    private final ScoreboardInfo scoreboard;
    private final Node source;
    private final WorldInfo world;

    public MapManifest(Set<IGameModuleConfig<?>> modules, ScoreboardInfo scoreboard, Node source, WorldInfo world) {
        this.modules = modules != null          ? modules       : new LinkedHashSet<>();
        this.scoreboard = scoreboard != null    ? scoreboard    : new ScoreboardInfo();
        this.source = this.detach(source);
        this.world = world != null              ? world         : new WorldInfo();
    }

    public Set<IGameModuleConfig<?>> getModules() {
        return new LinkedHashSet<>(this.modules);
    }

    public ScoreboardInfo getScoreboard() {
        return this.scoreboard;
    }

    public Node getSource() {
        return this.source;
    }

    public WorldInfo getWorld() {
        return this.world;
    }

    public boolean hasSource() {
        return this.source != null;
    }

    private Node detach(Node source) {
        source.detach();
        return source;
    }
}
