package pl.themolka.arcade.parser;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParsersFile {
    public static final String DEFAULT_FILENAME = "parsers.xml";

    private final Document document;

    public ParsersFile(ArcadePlugin plugin, InputStream input) throws DOMException, IOException {
        this.document = plugin.getDomEngines().forFile(DEFAULT_FILENAME).read(input);
    }

    public Document getDocument() {
        return this.document;
    }

    public List<Class<? extends Parser<?>>> getParsers() {
        return this.getParsers(this.getDocument());
    }

    public List<Class<? extends Parser<?>>> getParsers(Document document) {
        return this.getParsers(document.getRoot());
    }

    public List<Class<? extends Parser<?>>> getParsers(Node parent) {
        List<Class<? extends Parser<?>>> parsers = new ArrayList<>();

        for (Node node : parent.children("parser")) {
            try {
                Class<?> clazz = Class.forName(node.propertyValue("class"));
                if (Parser.class.isAssignableFrom(clazz)) {
                    parsers.add((Class<? extends Parser<?>>) clazz);
                }
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

        return parsers;
    }
}
