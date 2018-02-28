package pl.themolka.arcade.dom.engine;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Converting .properties files into DOM objects. All properties will be stored
 * as {@link Node}s under the root {@link Node}.
 */
@FileExtensions("properties")
public class PropertiesEngine implements DOMEngine {
    @Override
    public Document read(InputStream stream) throws DOMException, IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return this.read(properties);
    }

    @Override
    public Document read(Reader reader) throws DOMException, IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return this.read(properties);
    }

    private Document read(Properties properties) {
        return Document.create(this.convert(properties));
    }

    private Node convert(Properties properties) {
        List<Node> children = new ArrayList<>();
        for (String name : properties.stringPropertyNames()) {
            children.add(Node.ofPrimitive(name, properties.getProperty(name)));
        }

        return Node.ofChildren(Properties.class.getSimpleName(), children);
    }
}
