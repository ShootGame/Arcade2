package pl.themolka.arcade.map;

import org.apache.commons.lang3.builder.Builder;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.IGameModuleConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public class MapManifestBuilder implements Builder<MapManifest> {
    private Set<IGameModuleConfig<?>> modules;
    private ScoreboardInfo scoreboard;
    private Node source;
    private WorldInfo world;

    @Override
    public MapManifest build() {
        return new MapManifest(this.modules(),
                               this.scoreboard(),
                               this.source(),
                               this.world());
    }

    public Set<IGameModuleConfig<?>> modules() {
        return new LinkedHashSet<>(this.modules);
    }

    public MapManifestBuilder modules(Set<IGameModuleConfig<?>> modules) {
        this.modules = modules;
        return this;
    }

    public ScoreboardInfo scoreboard() {
        return this.scoreboard;
    }

    public MapManifestBuilder scoreboard(ScoreboardInfo scoreboard) {
        this.scoreboard = scoreboard;
        return this;
    }

    public Node source() {
        return this.source;
    }

    public MapManifestBuilder source(Node source) {
        this.source = source;
        return this;
    }

    public WorldInfo world() {
        return this.world;
    }

    public MapManifestBuilder world(WorldInfo world) {
        this.world = world;
        return this;
    }
}
