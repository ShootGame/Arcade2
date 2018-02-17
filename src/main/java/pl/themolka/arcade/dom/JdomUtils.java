package pl.themolka.arcade.dom;

import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public final class JdomUtils {
    private JdomUtils() {
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
        Element element = new Element(node.getName());

        for (Property property : node.properties()) {
            element.setAttribute(toAttribute(property));
        }

        if (node.isPrimitive()) {
            element.addContent(node.getValue());
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
        String value = element.getValue();

        List<Property> properties = new ArrayList<>();
        for (Attribute attribute : element.getAttributes()) {
            properties.add(toProperty(attribute));
        }

        if (value != null) {
            return Node.ofPrimitive(element.getName(), properties, value);
        } else {
            List<Node> children = new ArrayList<>();
            for (Element child : element.getChildren()) {
                children.add(toNode(child));
            }

            return Node.ofChildren(element.getName(), properties, children);
        }
    }
}
