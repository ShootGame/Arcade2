package pl.themolka.arcade.parser.type;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Silent;
import pl.themolka.arcade.parser.number.DoubleParser;

import java.util.Collections;
import java.util.List;

@Silent
@Produces(Double.class)
public class PercentageParser extends AbstractParser<Double>
                              implements InstallableParser {
    public static final double MIN_VALUE = 0.0D;
    public static final double MAX_VALUE = 1.0D;

    public static final String PERCENTAGE = "%";

    private DoubleParser doubleParser;

    @Override
    public void install(ParserContext context) {
        this.doubleParser = context.of(DoubleParser.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a percentage between " + MIN_VALUE + " and " + MAX_VALUE);
    }

    @Override
    protected ParserResult<Double> parse(Element element, String name, String value) throws ParserException {
        double result;
        if (value.contains(PERCENTAGE)) {
            // For input type "33%"
            result = this.doubleParser.parseWithDefinition(element, name, value.replace(PERCENTAGE, "")).orFail() / 100D;
        } else {
            // For input type "0.33"
            result = this.doubleParser.parseWithDefinition(element, name, value).orFail();
        }

        if (result < MIN_VALUE) {
            throw this.fail(element, name, value, "Percentage smaller than 0%");
        } else if (result > MAX_VALUE) {
            throw this.fail(element, name, value, "Percentage greater than 100%");
        }

        return ParserResult.fine(element, name, value, result);
    }
}
