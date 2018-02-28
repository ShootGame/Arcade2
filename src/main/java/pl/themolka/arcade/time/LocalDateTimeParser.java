package pl.themolka.arcade.time;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Set;

@Produces(LocalDateTime.class)
public class LocalDateTimeParser extends ElementParser<LocalDateTime> {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a date time, such as '2011-12-03T10:15:30'");
    }

    @Override
    protected ParserResult<LocalDateTime> parseElement(Element element, String name, String value) throws ParserException {
        try {
            return ParserResult.fine(element, name, value, LocalDateTime.parse(value, FORMATTER));
        } catch (DateTimeParseException ex) {
            throw this.fail(element, name, value, "Illegal date time format", ex);
        }
    }
}
