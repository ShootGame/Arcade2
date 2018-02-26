package pl.themolka.arcade.dom.engine;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.SAXEngine;
import org.jdom2.input.sax.SAXHandlerFactory;
import org.jdom2.located.LocatedJDOMFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import pl.themolka.arcade.dom.Cursor;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

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
@FileExtensions({"xml"})
public class JDOMEngine extends LocatedJDOMFactory
                        implements SAXHandlerFactory, XMLEngine {
    private final SAXEngine sax;

    public JDOMEngine() {
        SAXBuilder builder = new SAXBuilder();
        builder.setJDOMFactory(this);
        builder.setSAXHandlerFactory(this);
        this.sax = builder;
    }

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
        Node node = Node.of(jdom.getName());

        String maybeValue = jdom.getValue();
        if (children.isEmpty() && maybeValue != null) {
            // primitive value
            node.setValue(jdom.getValue());
        } else {
            // children
            List<Node> parsed = new ArrayList<>();
            for (Element child : children) {
                parsed.add(this.convert(child));
            }

            node.add(parsed);
        }

        // properties
        for (Attribute attribute : jdom.getAttributes()) {
            node.setProperty(Property.of(attribute.getName(), attribute.getValue()));
        }

        // location
        if (jdom instanceof JDOMElement) {
            JDOMElement located = (JDOMElement) jdom;

            Cursor start = located.getStartCursor();
            Cursor end = located.getEndCursor();

            if (start != null && end != null) {
                node.locate(start, end);
            }
        }

        return node;
    }

    //
    // JDOMFactory
    //

    @Override
    public Element element(int line, int col, String name, Namespace namespace) {
        return new JDOMElement(name, namespace);
    }

    @Override
    public Element element(int line, int col, String name) {
        return new JDOMElement(name);
    }

    @Override
    public Element element(int line, int col, String name, String uri) {
        return new JDOMElement(name, uri);
    }

    @Override
    public Element element(int line, int col, String name, String prefix, String uri) {
        return new JDOMElement(name, prefix, uri);
    }

    //
    // SAXHandler
    //

    @Override
    public org.jdom2.input.sax.SAXHandler createSAXHandler(org.jdom2.JDOMFactory factory) {
        return new JDOMEngine.SAXHandler(factory);
    }

    private class SAXHandler extends org.jdom2.input.sax.SAXHandler {
        SAXHandler(org.jdom2.JDOMFactory factory) {
            super(factory);
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            super.startElement(namespaceURI, localName, qName, atts);

            Element element = this.getCurrentElement();
            if (element instanceof JDOMElement) {
                ((JDOMElement) element).setStartCursor(this.createCursor(this.getDocumentLocator()));
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            Element element = this.getCurrentElement();
            if (element instanceof JDOMElement) {
                ((JDOMElement) element).setEndCursor(this.createCursor(this.getDocumentLocator()));
            }

            super.endElement(namespaceURI, localName, qName);
        }

        Cursor createCursor(Locator locator) {
            int line = locator.getLineNumber();
            int column = locator.getColumnNumber();

            return line != -1 && column != -1 ? new Cursor(line, column) : null;
        }
    }
}
