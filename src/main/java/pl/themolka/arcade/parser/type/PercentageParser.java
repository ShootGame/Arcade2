package pl.themolka.arcade.parser.type;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.parser.Silent;
import pl.themolka.arcade.util.Percentage;

import java.util.Collections;
import java.util.Set;

public abstract class PercentageParser extends ElementParser<Percentage>
                                       implements InstallableParser {
    private Parser<Double> percentageParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.percentageParser = context.type(Double.class);
    }

    @Override
    protected Result<Percentage> parseElement(Element element, String name, String value) throws ParserException {
        double result;
        if (StringUtils.contains(value, Percentage.SYMBOL)) {
            result = this.percentageParser.parseWithDefinition(element, name,
                    StringUtils.remove(value, Percentage.SYMBOL)).orFail() / 100D;
        } else {
            result = this.percentageParser.parseWithDefinition(element, name, value).orFail();
        }

        try {
            return Result.fine(element, name, value, this.parse(result));
        } catch (IllegalArgumentException ex) {
            throw this.fail(element, name, value, ex.getMessage(), ex);
        }
    }

    protected abstract Percentage parse(double input);

    @Produces(Percentage.class)
    public static class Finite extends PercentageParser {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("a percentage between 0% and 100%");
        }

        @Override
        protected Percentage parse(double input) {
            return Percentage.finite(input);
        }
    }

    @Silent // Don't register this parser, because Finite is the default
    @Produces(Percentage.class)
    public static class Infinite extends PercentageParser {
        @Override
        public Set<Object> expect() {
            return Collections.singleton("a percentage");
        }

        @Override
        protected Percentage parse(double input) {
            return Percentage.infinite(input);
        }
    }
}
