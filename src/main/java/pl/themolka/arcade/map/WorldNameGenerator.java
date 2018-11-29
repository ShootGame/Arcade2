/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
