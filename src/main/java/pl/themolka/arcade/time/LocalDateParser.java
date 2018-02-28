package pl.themolka.arcade.time;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Set;

@Produces(LocalDate.class)
public class LocalDateParser extends ElementParser<LocalDate> {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a date, such as '2011-12-03'");
    }

    @Override
    protected ParserResult<LocalDate> parseElement(Element element, String name, String value) throws ParserException {
        try {
            return ParserResult.fine(element, name, value, LocalDate.parse(value, FORMATTER));
        } catch (DateTimeParseException ex) {
            throw this.fail(element, name, value, "Illegal date format", ex);
        }
    }
}
