package pl.themolka.arcade.parser;

import com.google.common.collect.ImmutableMap;
import pl.themolka.arcade.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;

public class BooleanParser implements ElementParser<Boolean> {
    public static final String[] TRUE = new String[] {
            "true", "1", "+", "yes", "allow", "on", "enable", "enabled"};
    public static final String[] FALSE = new String[] {
            "false", "0", "-", "no", "deny", "off", "disable", "disabled"};

    private static final Map<String, Boolean> values;

    static {
        Map<String, Boolean> factory = new LinkedHashMap<>();
        collect(factory, TRUE, true);
        collect(factory, FALSE, false);

        values = ImmutableMap.copyOf(factory);
    }

    private static void collect(Map<String, Boolean> map, String[] array, boolean value) {
        for (String key : array) {
            map.put(key, value);
        }
    }

    @Override
    public Boolean parse(Element element, String value) throws ParserException {
        Boolean result = values.get(value);
        if (result != null) {
            return result;
        }

        return this.exception(element, value, Boolean.toString(true) + "/" + Boolean.toString(false));
    }
}
