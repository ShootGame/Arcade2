package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Node;

/**
 * Precise and playable map description. This class doesn't hold an
 * {@link OfflineMap}.
 */
public class MapManifest {
    private final ModulesInfo modules;
    private final ScoreboardInfo scoreboard;
    private final Node source;
    private final WorldInfo world;

    public MapManifest(ModulesInfo modules, ScoreboardInfo scoreboard, Node source, WorldInfo world) {
        this.modules = modules != null          ? modules       : new ModulesInfo();
        this.scoreboard = scoreboard != null    ? scoreboard    : new ScoreboardInfo();
        this.source = this.detach(source);
        this.world = world != null              ? world         : new WorldInfo();
    }

    public ModulesInfo getModules() {
        return this.modules;
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
