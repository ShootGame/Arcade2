package pl.themolka.arcade.map;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.number.LongParser;
import pl.themolka.arcade.parser.type.BooleanParser;

import java.util.Collections;
import java.util.List;

@Produces(MapTime.class)
public class MapTimeParser extends NodeParser<MapTime>
                           implements InstallableParser {
    public static final boolean DEFAULT_IS_LOCKED = false;
    public static final long DEFAULT_TIME_TICKS = MapTime.defaultTime().getTicks();

    private BooleanParser booleanParser;
    private EnumParser<MapTimeConstants> contantParser;
    private LongParser longParser;

    @Override
    public void install(ParserContext context) {
        this.booleanParser = context.of(BooleanParser.class);
        this.contantParser = context.enumType(MapTimeConstants.class);
        this.longParser = context.of(LongParser.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a map time");
    }

    @Override
    protected ParserResult<MapTime> parsePrimitive(Node node, String name, String value) throws ParserException {
        MapTime result = MapTime.ofTicks(this.parseTicks(node, name, value));
        result.setLocked(this.booleanParser.parse(node.property("locked", "lock")).orDefault(DEFAULT_IS_LOCKED));
        return ParserResult.fine(node, name, value, result);
    }

    private long parseTicks(Element element, String name, String value) throws ParserException {
        MapTimeConstants constant = this.contantParser.parseWithDefinition(element, name, value).orNull();
        if (constant != null) {
            return constant.ticks();
        }

        return this.longParser.parseWithDefinition(element, name, value).orDefault(DEFAULT_TIME_TICKS);
    }
}
