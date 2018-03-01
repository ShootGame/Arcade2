package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Cursor;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Selection;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Ref.class)
public class RefParser extends ElementParser<Ref> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("an ID reference");
    }

    @Override
    protected ParserResult<Ref> parseElement(Element element, String name, String value) throws ParserException {
        if (!this.validId(value)) {
            throw this.fail(element, name, value, "Invalid ID syntax");
        }

        Ref<?> ref = Ref.of(value);

        Cursor start = null;
        Cursor end = null;

        if (ref.isSelectable()) {
            Selection selection = ref.select();
            start = selection.getStart();
            end = selection.getEnd();
        }

        if (start != null && end != null) {
            ref.locate(start, end);
        }

        return ParserResult.fine(element, name, value, ref);
    }

    private boolean validId(String id) throws ParserException {
        return Ref.ID_PATTERN.matcher(id).matches();
    }
}
