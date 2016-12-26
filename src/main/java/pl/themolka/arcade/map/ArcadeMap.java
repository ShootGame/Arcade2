package pl.themolka.arcade.map;

import org.bukkit.World;

public class ArcadeMap {
    private final OfflineMap mapInfo;

    private World world;
    private String worldName;

    public ArcadeMap(OfflineMap mapInfo) {
        this.mapInfo = mapInfo;
    }

    public OfflineMap getMapInfo() {
        return this.mapInfo;
    }

    public World getWorld() {
        return this.world;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
