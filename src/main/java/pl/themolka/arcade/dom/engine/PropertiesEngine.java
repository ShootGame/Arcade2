package pl.themolka.arcade.dom.engine;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Namespace;
import pl.themolka.arcade.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Converting .properties files into DOM objects. All properties will be stored
 * as {@link Node}s under the root {@link Node}.
 */
@FileExtensions("properties")
public class PropertiesEngine implements DOMEngine {
    public static final Namespace NAMESPACE = Namespace.getDefault();

    @Override
    public Document read(InputStream stream) throws DOMException, IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return this.read(null, properties);
    }

    @Override
    public Document read(Reader reader) throws DOMException, IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return this.read(null, properties);
    }

    private Document read(Path path, Properties properties) {
        return Document.create(path, this.convert(properties));
    }

    private Node convert(Properties properties) {
        List<Node> children = new ArrayList<>();
        for (String name : properties.stringPropertyNames()) {
            children.add(Node.ofPrimitive(NAMESPACE, name, properties.getProperty(name)));
        }

        return Node.ofChildren(NAMESPACE, Properties.class.getSimpleName(), children);
    }
}
