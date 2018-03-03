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
}
