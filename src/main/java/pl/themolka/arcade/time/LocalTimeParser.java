package pl.themolka.arcade.time;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Set;

@Produces(LocalTime.class)
public class LocalTimeParser extends ElementParser<LocalTime> {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a time, such as '10:15' or '10:15:30'");
    }

    @Override
    protected Result<LocalTime> parseElement(Element element, String name, String value) throws ParserException {
        try {
            return Result.fine(element, name, value, LocalTime.parse(value, FORMATTER));
        } catch (DateTimeParseException ex) {
            throw this.fail(element, name, value, "Illegal time format", ex);
        }
    }
}
