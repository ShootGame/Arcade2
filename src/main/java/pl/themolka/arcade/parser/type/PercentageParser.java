package pl.themolka.arcade.parser.type;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Silent;
import pl.themolka.arcade.parser.number.DoubleParser;
import pl.themolka.arcade.util.Percentage;

import java.util.Collections;
import java.util.List;

public abstract class PercentageParser extends AbstractParser<Percentage>
                              implements InstallableParser {
    private DoubleParser doubleParser;

    @Override
    public void install(ParserContext context) {
        this.doubleParser = context.of(DoubleParser.class);
    }

    @Override
    protected ParserResult<Percentage> parse(Element element, String name, String value) throws ParserException {
        double result;
        if (StringUtils.contains(value, Percentage.SYMBOL)) {
            result = this.doubleParser.parseWithDefinition(element, name,
                    StringUtils.remove(value, Percentage.SYMBOL)).orFail() / 100D;
        } else {
            result = this.doubleParser.parseWithDefinition(element, name, value).orFail();
        }

        try {
            return ParserResult.fine(element, name, value, this.parse(result));
        } catch (IllegalArgumentException ex) {
            throw this.fail(element, name, value, ex.getMessage(), ex);
        }
    }

    protected abstract Percentage parse(double input);

    @Produces(Percentage.class)
    public static class Finite extends PercentageParser {
        @Override
        public List<Object> expect() {
            return Collections.singletonList("a percentage between 0% and 100%");
        }

        @Override
        protected Percentage parse(double input) {
            return Percentage.finite(input);
        }
    }

    @Silent // Don't register this parser
    @Produces(Percentage.class)
    public static class Infinite extends PercentageParser {
        @Override
        public List<Object> expect() {
            return Collections.singletonList("a percentage");
        }

        @Override
        protected Percentage parse(double input) {
            return Percentage.infinite(input);
        }
    }
}
