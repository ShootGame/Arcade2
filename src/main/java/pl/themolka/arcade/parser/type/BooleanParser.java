package pl.themolka.arcade.parser.type;

import com.google.common.collect.ImmutableMap;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BooleanParser extends AbstractParser<Boolean> {
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

    public BooleanParser() {
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a boolean");
    }

    @Override
    protected ParserResult<Boolean> parse(Element element, String name, String value) throws ParserException {
        Boolean result = values.get(value.toLowerCase());
        if (result != null) {
            return ParserResult.fine(element, name, value, result);
        }

        throw this.fail(element, name, value, "Unknown boolean property");
    }
}
