package pl.themolka.arcade.xml;

import org.jdom2.Element;
import pl.themolka.arcade.dom.Cursor;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Namespace;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.dom.engine.JDOMElement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Uses JDOM.
 */
@Deprecated
public final class XML {
    private XML() {
    }

    public static Document read(Path path, org.jdom2.Document jdom) throws DOMException {
        if (!jdom.hasRootElement()) {
            throw new DOMException(null, "The root node is not defined.");
        }

        return Document.create(path, XML.convert(jdom.getRootElement()));
    }

    public static Node convert(org.jdom2.Element jdom) {
        List<Element> children = jdom.getChildren();
        Node node = Node.of(XML.convert(jdom.getNamespace()), jdom.getName());

        String maybeValue = jdom.getValue();
        if (children.isEmpty() && maybeValue != null) {
            // primitive value
            node.setValue(jdom.getValue());
        } else {
            // children
            List<Node> parsed = new ArrayList<>();
            for (org.jdom2.Element child : children) {
                parsed.add(XML.convert(child));
            }

            node.add(parsed);
        }

        // properties
        for (org.jdom2.Attribute attribute : jdom.getAttributes()) {
            node.setProperty(XML.convert(attribute));
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
        return Property.of(XML.convert(attribute.getNamespace()), attribute.getName(), attribute.getValue());
    }
}
