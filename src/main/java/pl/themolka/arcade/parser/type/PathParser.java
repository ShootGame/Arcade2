package pl.themolka.arcade.parser.type;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

@Produces(Path.class)
public class PathParser extends ElementParser<Path> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("path");
    }

    @Override
    protected Result<Path> parseElement(Element element, String name, String value) throws ParserException {
        try {
            return Result.fine(element, name, value, Paths.get(value));
        } catch (InvalidPathException ex) {
            throw this.fail(element, name, value, "Invalid path syntax", ex);
        }
    }
}
