package pl.themolka.arcade.parser.type;

import com.google.common.collect.ImmutableMap;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Produces(Boolean.class)
public class BooleanParser extends ElementParser<Boolean> {
    private static final Map<String, Boolean> values = ImmutableMap.<String, Boolean>builder()
            .putAll(Types.TRUE.toMap())
            .putAll(Types.FALSE.toMap())
            .build();

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

    enum Types {
        TRUE(true, "true", "1", "+", "yes", "allow", "on", "enable", "enabled"),
        FALSE(false, "false", "0", "-", "no", "deny", "off", "disable", "disabled");

        final boolean asBoolean;
        final String[] values;

        Types(boolean asBoolean, String... values) {
            this.asBoolean = asBoolean;
            this.values = values;
        }

        Map<String, Boolean> toMap() {
            Map<String, Boolean> collect = new HashMap<>();
            for (String value : this.values) {
                collect.put(value, this.asBoolean);
            }

            return collect;
        }
    }
}
