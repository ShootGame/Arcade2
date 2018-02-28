package pl.themolka.arcade.parser.type;

import com.google.common.collect.ImmutableMap;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Produces(Boolean.class)
public class BooleanParser extends ElementParser<Boolean> {
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
    public Set<Object> expect() {
        return Collections.singleton("a boolean");
    }

    @Override
    protected ParserResult<Boolean> parseElement(Element element, String name, String value) throws ParserException {
        Boolean result = values.get(value.toLowerCase());
        if (result != null) {
            return ParserResult.fine(element, name, value, result);
        }

        throw this.fail(element, name, value, "Unknown boolean property");
    }
}
