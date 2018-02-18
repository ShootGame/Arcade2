package pl.themolka.arcade.dom;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.located.LocatedElement;

import java.util.ArrayList;
import java.util.List;

public final class JdomUtils {
    private JdomUtils() {
    }

    public static LocatedElement attachCursor(LocatedElement element, Cursor cursor) {
        if (cursor != null) {
            element.setLine(cursor.getLine());
            element.setColumn(cursor.getColumn());
        }

        return element;
    }

    public static Cursor cursor(LocatedElement element) {
        return new Cursor(element.getLine(), element.getColumn());
    }

    public static Attribute toAttribute(Property property) {
        return new Attribute(property.getName(), property.getValue());
    }

    public static Document toDocument(org.jdom2.Document jdom) {
        return jdom.hasRootElement() ? Document.create(toNode(jdom.getRootElement()))
                                     : Document.create();
    }

    public static org.jdom2.Document toDocument(Document document) {
        return document.hasRoot() ? new org.jdom2.Document(toElement(document.getRoot()))
                                  : new org.jdom2.Document();
    }

    public static Element toElement(Node node) {
        Element element = createElement(node);

        // attach location
        if (element instanceof LocatedElement) {
            element = attachCursor((LocatedElement) element, node.getLocation());
        }

        // attach properties
        for (Property property : node.properties()) {
            element.setAttribute(toAttribute(property));
        }

        // attach body
        if (node.isPrimitive()) {
            element.setText(node.getValue());
        } else if (node.isTree()) {
            List<Element> children = new ArrayList<>();
            for (Node child : node.children()) {
                children.add(toElement(child));
            }

            element.addContent(children);
        }

        return element;
    }

    public static Property toProperty(Attribute attribute) {
        return Property.of(attribute.getName(), attribute.getValue());
    }

    public static Node toNode(Element element) {
        List<Element> children = element.getChildren();

        // properties
        List<Property> properties = new ArrayList<>();
        for (Attribute attribute : element.getAttributes()) {
            properties.add(toProperty(attribute));
        }

        // body
        Node node;
        if (children.isEmpty()) {
            node = Node.ofPrimitive(element.getName(), properties, element.getValue());
        } else {
            List<Node> nodeChildren = new ArrayList<>();
            for (Element child : element.getChildren()) {
                nodeChildren.add(toNode(child));
            }

            node = Node.ofChildren(element.getName(), properties, nodeChildren);
        }

        // cursor
        if (element instanceof LocatedElement) {
            node.setLocation(cursor((LocatedElement) element));
        }

        return node;
    }

    //
    // Helper Methods
    //

    private static Element createElement(Node node) {
        return node.hasLocation() ? new LocatedElement(node.getName())
                                  : new Element(node.getName());
    }
}
