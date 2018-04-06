package pl.themolka.arcade.region;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

@Produces(RegionFieldStrategy.class)
public class RegionFieldStrategyParser extends ElementParser<RegionFieldStrategy>
                                       implements InstallableParser {
    private Parser<String> textParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.textParser = context.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("region field strategy");
    }

    @Override
    protected ParserResult<RegionFieldStrategy> parseElement(Element element, String name, String value) throws ParserException {
        String text = this.textParser.parseWithDefinition(element, name, value).orFail();

        try {
            Field field = RegionFieldStrategy.class.getField(this.normalizeFieldName(text));
            field.setAccessible(true);

            if (RegionFieldStrategy.class.isAssignableFrom(field.getType())) {
                return ParserResult.fine(element, name, value, (RegionFieldStrategy) field.get(null));
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        throw this.fail(element, name, value, "Unknown region field strategy type");
    }

    protected String normalizeFieldName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
