/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.dom.engine;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.SAXEngine;
import org.jdom2.input.sax.SAXHandlerFactory;
import org.jdom2.located.LocatedJDOMFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import pl.themolka.arcade.dom.Content;
import pl.themolka.arcade.dom.Cursor;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Namespace;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            return this.read(file.toPath(), this.sax.build(file));
        } catch (JDOMException jdom) {
            throw new DOMException((Content) null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(InputSource source) throws DOMException, IOException {
        try {
            return this.read(null, this.sax.build(source));
        } catch (JDOMException jdom) {
            throw new DOMException((Content) null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(InputStream stream) throws DOMException, IOException {
        try {
            return this.read(null, this.sax.build(stream));
        } catch (JDOMException jdom) {
            throw new DOMException((Content) null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(Reader reader) throws DOMException, IOException {
        try {
            return this.read(null, this.sax.build(reader));
        } catch (JDOMException jdom) {
            throw new DOMException((Content) null, jdom.getMessage(), jdom);
        }
    }

    @Override
    public Document read(URI uri) throws DOMException, IOException {
        try {
            return this.read(Paths.get(uri), this.sax.build(uri.toURL()));
        } catch (JDOMException jdom) {
            throw new DOMException((Content) null, jdom.getMessage(), jdom);
        }
    }

    public SAXEngine sax() {
        return this.sax;
    }

    private Document read(Path path, org.jdom2.Document jdom) throws DOMException {
        if (!jdom.hasRootElement()) {
            throw new DOMException((Content) null, "The root node is not defined.");
        }

        return Document.create(path, JDOMEngine.convert(jdom.getRootElement()));
    }



    //
    // JDOMFactory
    //

    @Override
    public org.jdom2.Element element(int line, int col, String name, org.jdom2.Namespace namespace) {
        return new JDOMElement(name, namespace);
    }

    @Override
    public org.jdom2.Element element(int line, int col, String name) {
        return new JDOMElement(name);
    }

    @Override
    public org.jdom2.Element element(int line, int col, String name, String uri) {
        return new JDOMElement(name, uri);
    }

    @Override
    public org.jdom2.Element element(int line, int col, String name, String prefix, String uri) {
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

            org.jdom2.Element element = this.getCurrentElement();
            if (element instanceof JDOMElement) {
                ((JDOMElement) element).setStartCursor(this.createCursor(this.getDocumentLocator()));
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            org.jdom2.Element element = this.getCurrentElement();
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

    //
    // Converting
    //

    public static Node convert(org.jdom2.Element jdom) {
        List<Element> children = jdom.getChildren();
        Node node = Node.of(JDOMEngine.convert(jdom.getNamespace()), jdom.getName());

        String maybeValue = jdom.getValue();
        if (children.isEmpty() && maybeValue != null) {
            // primitive value
            node.setValue(jdom.getValue());
        } else {
            // children
            List<Node> parsed = new ArrayList<>();
            for (org.jdom2.Element child : children) {
                parsed.add(JDOMEngine.convert(child));
            }

            node.add(parsed);
        }

        // properties
        for (org.jdom2.Attribute attribute : jdom.getAttributes()) {
            node.setProperty(JDOMEngine.convert(attribute));
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

    public static Namespace convert(org.jdom2.Namespace namespace) {
        return Namespace.of(namespace.getPrefix(), namespace.getURI());
    }

    public static Property convert(org.jdom2.Attribute attribute) {
        return Property.of(JDOMEngine.convert(attribute.getNamespace()), attribute.getName(), attribute.getValue());
    }
}
