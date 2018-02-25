package pl.themolka.arcade.time;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.AbstractParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(Time.class)
public class TimeParser extends AbstractParser<Time> {
    @Override
    public List<Object> expect() {
        return Collections.singletonList("a time");
    }

    @Override
    protected ParserResult<Time> parse(Element element, String name, String value) throws ParserException {
        Time time = Time.parseTime(value, null);
        if (time != null) {
            return ParserResult.fine(element, name, value, time);
        }

        throw this.fail(element, name, value, "Illegal time format");
    }
}
