package pl.themolka.arcade.map;

import org.bukkit.World;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameHolder;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Representation of a playable map based on the given {@link OfflineMap} and
 * {@link MapManifest}.
 */
public class ArcadeMap implements GameHolder {
    private final OfflineMap mapInfo;
    private final MapManifest manifest;

    private Reference<Game> game;
    private Reference<World> world;
    private String worldName;

    public ArcadeMap(OfflineMap mapInfo, MapManifest manifest) {
        this.mapInfo = mapInfo;
        this.manifest = manifest;
    }

    @Override
    public Game getGame() {
        return this.game != null ? this.game.get() : null;
    }

    @Override
    public ArcadeMap getMap() {
        return this;
    }

    public OfflineMap getMapInfo() {
        return this.mapInfo;
    }

    public MapManifest getManifest() {
        return this.manifest;
    }

    public World getWorld() {
        return this.world != null ? this.world.get() : null;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setGame(Game game) {
        if (this.game == null) {
            this.game = new WeakReference<>(game);
        }
    }

    public void setWorld(World world) {
        this.world = new WeakReference<>(world);
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
