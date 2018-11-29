package pl.themolka.arcade.spawn;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

@Produces(Direction.class)
public class DirectionParser extends ElementParser<Direction>
                             implements InstallableParser {
    private Parser<String> textParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.textParser = context.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("direction mode");
    }

    @Override
    protected Result<Direction> parseElement(Element element, String name, String value) throws ParserException {
        String text = this.textParser.parseWithDefinition(element, name, value).orFail();

        try {
            Field field = Direction.class.getField(this.normalizeFieldName(text));
            field.setAccessible(true);

            if (Direction.class.isAssignableFrom(field.getType())) {
                return Result.fine(element, name, value, (Direction) field.get(null));
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        throw this.fail(element, name, value, "Unknown direction mode");
    }

    protected String normalizeFieldName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
