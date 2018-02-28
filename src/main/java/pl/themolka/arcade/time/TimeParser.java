package pl.themolka.arcade.time;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Time.class)
public class TimeParser extends ElementParser<Time> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("a time duration");
    }

    @Override
    protected ParserResult<Time> parseElement(Element element, String name, String value) throws ParserException {
        Time time = Time.parseTime(value, null);
        if (time != null) {
            return ParserResult.fine(element, name, value, time);
        }

        throw this.fail(element, name, value, "Illegal time format");
    }
}
