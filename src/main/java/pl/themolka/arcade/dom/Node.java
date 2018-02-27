package pl.themolka.arcade.dom;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Node extends Element implements Locatable, Parent<Node>, Propertable {
    enum Type {
        PRIMITIVE, TREE, UNKNOWN
    }

    private Node(String name) {
        super(name);
    }

    private Node(String name, String value) {
        super(name, value);
    }

    private final Properties properties = new Properties();
    private final List<Node> children = new ArrayList<>();

    private Selection location;
    private Node parent;

    @Override
    public boolean add(Collection<Node> children) {
        if (children != null) {
            this.resetPrimitive(); // switch to the tree type if needed

            for (Node node : children) {
                this.attach(node);
            }

            return this.children.addAll(children);
        }

        return false;
    }

    @Override
    public void appendChildren(Iterable<Node> append, boolean deep) {
        if (append != null) {
            this.append(append, deep, false);
        }
    }

    @Override
    public void appendProperties(Iterable<Property> append) {
        if (append != null) {
            this.properties.appendProperties(append);

            for (Property property : append) {
                this.attach(property);
            }
        }
    }

    @Override
    public List<Node> children() {
        return new ArrayList<>(this.children);
    }

    @Override
    public List<Node> children(Iterable<String> names) {
        List<Node> results = new ArrayList<>();

        if (names != null) {
            for (Node child : this.children) {
                for (String name : names) {
                    if (name != null && child.getName().equals(name)) {
                        results.add(child);
                    }
                }
            }
        }

        return results;
    }

    @Override
    public int clearChildren() {
        for (Node child : this.children) {
            child.detach();
        }

        int count = this.children.size();
        this.children.clear();
        return count;
    }

    @Override
    public Node clone() {
        Node clone = (Node) super.clone();
        for (Property property : this.properties.clone().properties()) {
            clone.properties.appendProperties(property);
        }

        for (Node original : this.children) {
            clone.children.add(original.clone());
        }

        clone.location = this.location;
        clone.parent = this.parent;
        return clone;
    }

    @Override
    public boolean detach() {
        return this.setParent(null) != null;
    }

    @Override
    public Node firstChild() {
        return this.children.isEmpty() ? null : this.children.get(0);
    }

    @Override
    public Node firstChild(Iterable<String> names) {
        List<Node> children = this.children(names);
        return children.isEmpty() ? null : children.get(0);
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }

    @Override
    public boolean hasProperties() {
        return this.properties.hasProperties();
    }

    @Override
    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    @Override
    public boolean isSelectable() {
        return this.location != null;
    }

    @Override
    public Node lastChild() {
        int size = this.children.size();
        return size == 0 ? null : this.children.get(size - 1);
    }

    @Override
    public Node lastChild(Iterable<String> names) {
        List<Node> children = this.children(names);
        return children.isEmpty() ? null : children.get(children.size() - 1);
    }

    @Override
    public void locate(Cursor start, Cursor end) {
        this.location = Selection.between(start, end);
    }

    @Override
    public List<Property> properties() {
        return this.properties.properties();
    }

    @Override
    public List<Property> properties(Iterable<String> names) {
        return this.properties.properties(names);
    }

    @Override
    public Property property(Iterable<String> names) {
        return this.properties.property(names);
    }

    @Override
    public String propertyValue(Iterable<String> names) {
        return this.properties.propertyValue(names);
    }

    @Override
    public String propertyValue(Iterable<String> names, String def) {
        return this.properties.propertyValue(names, def);
    }

    @Override
    public boolean remove(Iterable<Node> children) {
        if (children == null || this.isEmpty()) {
            return false;
        }

        boolean result = true;
        for (Node child : children) {
            if (!this.children.remove(child)) {
                child.detach();
                result = false;
            }
        }

        return result;
    }

    @Override
    public boolean removeByName(Iterable<String> children) {
        if (children == null || this.isEmpty()) {
            return false;
        }

        boolean result = false;
        for (Node child : new ArrayList<>(this.children)) {
            for (String name : children) {
                if (name != null && child.getName().equals(name)) {
                    if (this.remove(child)) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Selection select() {
        if (this.location != null) {
            return this.location;
        } else if (this.hasParent()) {
            Selectable parent = this.getParent();
            if (parent.isSelectable()) {
                return parent.select();
            }
        }

        return null;
    }

    @Override
    public Node setParent(Node parent) {
        Node oldParent = this.parent;
        this.parent = parent;

        return oldParent;
    }

    @Override
    public Property setProperty(Property property) {
        Property oldProperty = this.properties.setProperty(property);
        if (property != null) {
            this.attach(property);
        }

        return oldProperty;
    }

    @Override
    public void setProperties(Iterable<Property> properties) {
        this.properties.setProperties(properties);
    }

    @Override
    public String setValue(String value) {
        this.resetTree();
        return super.setValue(value);
    }

    @Override
    public void sortChildren(Comparator<? super Node> comparator) {
        if (comparator != null) {
            this.children.sort(comparator);
        }
    }

    @Override
    public void sortProperties(Comparator<? super Property> comparator) {
        this.properties.sortProperties(comparator);
    }

    @Override
    public String toShortString() {
        return this.toString(true, false);
    }

    @Override
    public int unsetProperties() {
        return this.properties.unsetProperties();
    }

    @Override
    public boolean unsetProperty(Property property) {
        if (property != null && this.properties.unsetProperty(property)) {
            property.detach();
            return true;
        }

        return false;
    }

    @Override
    public boolean unsetProperty(String name) {
        if (name != null) {
            Property property = this.property(name);
            if (property != null) {
                property.detach();
                return this.unsetProperty(property);
            }
        }

        return false;
    }

    public void append(Iterable<Node> append, boolean deep, boolean properties) {
        if (append != null) {
            for (Node toAppend : append) {
                if (properties) {
                    this.appendProperties(toAppend.properties());
                }

                if (this.isPrimitive()) {
                    // Primitive types cannot hold children.
                    continue;
                }

                this.add(toAppend);
                if (deep) {
                    // Append again with its children if deep is true
                    toAppend.append(toAppend.children, true, properties);
                }
            }
        }
    }

    public void append(boolean deep, boolean properties, Node... append) {
        if (append != null) {
            this.append(Arrays.asList(append), deep, properties);
        }
    }

    public Type getType() {
        if (this.isPrimitive()) {
            return Type.PRIMITIVE;
        } else if (this.isTree()) {
            return Type.TREE;
        }

        return Type.UNKNOWN;
    }

    public boolean isPrimitive() {
        return this.hasValue();
    }

    public boolean isTree() {
        return !this.isEmpty();
    }

    @Override
    public String toString() {
        return this.toString(true, true);
    }

    public String toString(boolean children) {
        return this.toString(true, children);
    }

    public String toString(boolean properties, boolean children) {
        String nodeName = this.getName();

        // starting tag
        String starting = nodeName;
        if (properties && this.properties.hasProperties()) {
            starting += " " + StringUtils.join(this.properties, " ");
        }

        StringBuilder builder = new StringBuilder(this.toStringTag(false, starting));

        // primitive value
        String value = null;
        if (this.hasValue()) {
            String realValue = this.getValue();
            if (!realValue.isEmpty()) {
                value = realValue.trim();
            }
        }

        // children
        List<Node> node;
        if (children && !this.isEmpty()) {
            node = this.children();
        } else {
            node = Collections.emptyList();
        }

        // body
        if (value != null) {
            builder.append(value);
        } else if (!node.isEmpty()) {
            builder.append(StringUtils.join(node, " "));
        }

        // closing tag
        return builder.append(this.toStringTag(true, nodeName)).toString();
    }

    //
    // Helper Methods
    //

    private void attach(Child<Node> child) {
        child.setParent(this);
    }

    private boolean resetPrimitive() {
        boolean ok = this.isPrimitive();
        this.setValue(null);

        return ok;
    }

    private boolean resetTree() {
        boolean ok = this.isTree();
        this.clearChildren();

        return ok;
    }

    private String toStringTag(boolean closing, String tag) {
        return (closing ? "</" : "<") + tag + ">";
    }

    //
    // Instancing
    //

    public static Node of(String name) {
        return new Node(name);
    }

    public static Node of(String name, List<Property> properties) {
        Node node = of(name);
        node.setProperties(properties);
        return node;
    }

    public static Node ofPrimitive(String name, String value) {
        return new Node(name, value);
    }

    public static Node ofPrimitive(String name, List<Property> properties, String value) {
        Node node = ofPrimitive(name, value);
        node.setProperties(properties);
        return node;
    }

    public static Node ofChildren(String name, List<Node> children) {
        Node node = of(name);
        node.add(children);
        return node;
    }

    public static Node ofChildren(String name, List<Property> properties, List<Node> children) {
        Node node = ofChildren(name, children);
        node.setProperties(properties);
        return node;
    }
}
