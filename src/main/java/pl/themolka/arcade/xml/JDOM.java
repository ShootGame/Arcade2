package pl.themolka.arcade.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.located.LocatedElement;
import pl.themolka.arcade.dom.Cursor;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Converting our DOM objects into their JDOM representations.
 *
 * The Goal is to totally drop JDOM library from the source. We need to convert
 * our DOM objects to JDOM's to support legacy code. This class should be used
 * only for legacy purposes and ignored in new code.
 *
 * @deprecated Should be removed after the code is transformed to new parsers.
 */
@Deprecated
public final class JDOM {
    private JDOM() {
    }

    public static org.jdom2.Document from(Document document) {
        if (document != null) {
            Node root = document.hasRoot() ? document.getRoot() : null;
            return new org.jdom2.Document(from(root));
        }

        return null;
    }

    public static Element from(Node node) {
        if (node == null) {
            return null;
        }

        // attach location
        Element element;
        if (node.hasLocation()) {
            Cursor cursor = node.getLocation();

            LocatedElement located = new LocatedElement(node.getName());
            located.setLine(cursor.getLine());
            located.setColumn(cursor.getColumn());

            element = located;
        } else {
            element = new Element(node.getName());
        }

        // attach properties
        for (Property property : node.properties()) {
            element.setAttribute(new Attribute(property.getName(), property.getValue()));
        }

        // attach body
        if (node.isPrimitive()) {
            element.setText(node.getValue());
        } else if (node.isTree()) {
            List<Element> children = new ArrayList<>();
            for (Node child : node.children()) {
                children.add(from(child));
            }

            element.addContent(children);
        }

        return element;
    }
}
