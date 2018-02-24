package pl.themolka.arcade.dom;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.SAXEngine;
import org.jdom2.located.LocatedElement;
import org.jdom2.located.LocatedJDOMFactory;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Producing DOM objects from the JDOM library.
 */
public class JDOMEngine implements XMLEngine {
    private static final JDOMEngine defaultEngine = createDefault();

    private final SAXEngine sax;

    public JDOMEngine(SAXEngine sax) {
        this.sax = Objects.requireNonNull(sax, "sax cannot be null");
    }

    @Override
    public Document read(File file) throws DOMException, IOException {
        try {
            return this.read(this.sax.build(file));
        } catch (JDOMException jdom) {
            throw new DOMException(null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(InputSource source) throws DOMException, IOException {
        try {
            return this.read(this.sax.build(source));
        } catch (JDOMException jdom) {
            throw new DOMException(null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(InputStream stream) throws DOMException, IOException {
        try {
            return this.read(this.sax.build(stream));
        } catch (JDOMException jdom) {
            throw new DOMException(null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(Reader reader) throws DOMException, IOException {
        try {
            return this.read(this.sax.build(reader));
        } catch (JDOMException jdom) {
            throw new DOMException(null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(URL url) throws DOMException, IOException {
        try {
            return this.read(this.sax.build(url));
        } catch (JDOMException jdom) {
            throw new DOMException(null, jdom.getMessage(), jdom);
        }
    }

    public SAXEngine sax() {
        return this.sax;
    }

    private Document read(org.jdom2.Document jdom) throws DOMException {
        if (!jdom.hasRootElement()) {
            throw new DOMException(null, "The root node is not defined.");
        }

        return Document.create(this.convert(jdom.getRootElement()));
    }

    private Node convert(org.jdom2.Element jdom) {
        List<org.jdom2.Element> children = jdom.getChildren();

        Node node;
        if (children.isEmpty()) {
            // primitive value
            node = Node.ofPrimitive(jdom.getName(), jdom.getValue());
        } else {
            // children
            List<Node> parsedChildren = new ArrayList<>();
            for (Element child : children) {
                parsedChildren.add(this.convert(child));
            }

            node = Node.ofChildren(jdom.getName(), parsedChildren);
        }

        // properties
        for (Attribute attribute : jdom.getAttributes()) {
            node.setProperty(attribute.getName(), attribute.getValue());
        }

        // location
        if (jdom instanceof LocatedElement) {
            LocatedElement located = (LocatedElement) jdom;
            node.setLocation(new Cursor(located.getLine(), located.getColumn()));
        }

        return node;
    }

    //
    // Instancing
    //

    public static JDOMEngine getDefaultEngine() {
        return defaultEngine;
    }

    private static JDOMEngine createDefault() {
        try {
            SAXBuilder sax = new SAXBuilder();
            sax.setJDOMFactory(new LocatedJDOMFactory()); // so we have lines and columns attached
            return new JDOMEngine(sax.buildEngine());
        } catch (JDOMException jdom) {
            throw new RuntimeException(jdom);
        }
    }
}
