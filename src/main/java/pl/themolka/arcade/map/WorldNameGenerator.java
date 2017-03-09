package pl.themolka.arcade.map;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class WorldNameGenerator {
    public static final char SLUG_MAGIC_KEY = '_';

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
        char[] array = StringUtils.stripAccents(worldName).toCharArray();
        char[] result = new char[array.length];

        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (CharUtils.isAsciiAlphanumeric(c)) {
                result[i] = Character.toLowerCase(c);
            } else {
                result[i] = SLUG_MAGIC_KEY;
            }
        }

        return String.valueOf(result);
    }

    public void setMap(OfflineMap map) {
        this.map = map;
    }
}
