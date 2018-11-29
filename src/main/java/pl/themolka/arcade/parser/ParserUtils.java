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

package pl.themolka.arcade.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple parser utilities.
 */
public final class ParserUtils {
    public static final String ARRAY_SPLIT_KEY = ",";

    private ParserUtils() {
    }

    public static List<String> array(String input) {
        return array(input, -1);
    }

    public static List<String> array(String input, int maxItems) {
        if (input == null) {
            return null;
        }

        String[] split = input.split(ARRAY_SPLIT_KEY);

        List<String> array = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            String item = split[i].trim();
            if (!item.isEmpty() && (maxItems == -1 || maxItems > i)) {
                array.add(item);
            }
        }

        return array;
    }

    public static boolean ensureNotEmpty(Iterable<?> array) {
        return !array.iterator().hasNext();
    }
}
