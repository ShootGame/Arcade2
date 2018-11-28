package pl.themolka.arcade.map;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.util.FastUUID;

import java.util.Arrays;
import java.util.UUID;

public class WorldNameGenerator {
    public static final char SLUG_MAGIC_KEY = '_';

    private final FastUUID fastUUID = new FastUUID();

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
        if (randomId == null) {
            randomId = this.fastUUID.random();
        }

        return this.normalizeWorldName(randomId.toString().replace("-", ""));
    }

    public String normalizeWorldName(String worldName) {
        char[] array = StringUtils.stripAccents(worldName).toCharArray();
        char[] result = new char[array.length];

        int index = 0;
        for (int i = 0; i < array.length; i++) {
            char c = array[index];
            if (CharUtils.isAsciiAlphanumeric(c)) {
                result[i] = Character.toLowerCase(c);
            } else if (Character.isWhitespace(c)) {
                result[i] = SLUG_MAGIC_KEY;
            } else {
                result = Arrays.copyOf(result, result.length - 1);
                continue;
            }

            index++;
        }

        return String.valueOf(result);
    }
}
