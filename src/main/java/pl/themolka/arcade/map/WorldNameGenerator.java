package pl.themolka.arcade.map;

import org.apache.commons.lang3.CharUtils;

import java.util.UUID;

public class WorldNameGenerator {
    private OfflineMap map;

    public WorldNameGenerator() {
    }

    public WorldNameGenerator(ArcadeMap map) {
        this(map.getMapInfo());
    }

    public WorldNameGenerator(OfflineMap map) {
        this.map = map;
    }

    public OfflineMap getMap() {
        return this.map;
    }

    public String nextWorldName() {
        return this.nextWorldName(null, null);
    }

    public String nextWorldName(String name) {
        return this.nextWorldName(name, null);
    }

    public String nextWorldName(UUID randomId) {
        return this.nextWorldName(null, randomId);
    }

    public String nextWorldName(String name, UUID randomId) {
        if (name == null) {
            name = this.getMap().getDirectory().getName();
        }

        if (randomId == null) {
            randomId = UUID.randomUUID();
        }

        return this.normalizeWorldName(randomId.toString().replace("-", ""));
    }

    public String normalizeWorldName(String worldName) {
        char[] array = worldName.toCharArray();
        char[] result = new char[array.length];

        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (CharUtils.isAsciiAlphanumeric(c)) {
                result[i] = c;
            } else {
                result[i] = '_';
            }
        }

        return String.valueOf(result).toLowerCase();
    }

    public void setMap(OfflineMap map) {
        this.map = map;
    }
}
