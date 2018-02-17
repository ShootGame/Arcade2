package pl.themolka.arcade.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Node implements NamedValue {
    private final List<Property> properties = new ArrayList<>();
    private final List<Node> children = new ArrayList<>();

    private String name;
    private String value;

    private Node(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean hasValue() {
        return this.isPrimitive();
    }

    @Override
    public String setName(String name) {
        String oldName = this.name;
        if (name != null) {
            this.name = name;
        }

        return oldName;
    }

    @Override
    public String setValue(String value) {
        this.resetTree(); // switch to the primitive type if needed

        String oldValue = this.value;
        this.value = value;

        return oldValue;
    }

    public boolean add(Node... children) {
        return children != null && this.add(Arrays.asList(children));
    }

    public boolean add(Collection<Node> children) {
        if (children != null) {
            this.resetPrimitive(); // switch to the tree type if needed
            return this.children.addAll(children);
        }

        return false;
    }

    public Node firstChild() {
        return this.children.isEmpty() ? null : this.children.get(0);
    }

    public Node firstChild(Iterable<String> names) {
        List<Node> children = this.children(names);
        return children.isEmpty() ? null : children.get(0);
    }

    public Node firstChild(String... names) {
        return this.firstChild(Arrays.asList(names));
    }

    public Node lastChild() {
        int size = this.children.size();
        return size == 0 ? null : this.children.get(size - 1);
    }

    public Node lastChild(Iterable<String> names) {
        List<Node> children = this.children(names);
        return children.isEmpty() ? null : children.get(children.size() - 1);
    }

    public Node lastChild(String... names) {
        return this.lastChild(Arrays.asList(names));
    }

    public List<Node> children() {
        return new ArrayList<>(this.children);
    }

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

    public List<Node> children(String... names) {
        return this.children(Arrays.asList(names));
    }

    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    public boolean isPrimitive() {
        return this.value != null;
    }

    public boolean isTree() {
        return !this.children.isEmpty();
    }

    public List<Property> properties() {
        return new ArrayList<>(this.properties);
    }

    public List<Property> properties(Iterable<String> names) {
        List<Property> properties = new ArrayList<>();

        if (names != null) {
            for (Property property : this.properties) {
                for (String name : names) {
                    if (name != null && property.getName().equals(name)) {
                        properties.add(property);
                    }
                }
            }
        }

        return properties;
    }

    public List<Property> properties(String... names) {
        return this.properties(Arrays.asList(names));
    }

    public Property property(Iterable<String> names) {
        if (names != null) {
            for (Property property : this.properties) {
                for (String name : names) {
                    if (name != null && property.getName().equals(name)) {
                        return property;
                    }
                }
            }
        }

        return null;
    }

    public Property property(String... names) {
        return this.property(Arrays.asList(names));
    }

    public boolean remove(Node... children) {
        if (children == null || this.isEmpty()) {
            return false;
        }

        boolean result = true;
        for (Node child : children) {
            if (!this.children.remove(child)) {
                result = false;
            }
        }

        return result;
    }

    public Property setProperty(Property property) {
        Property oldProperty = null;
        if (property != null) {
            oldProperty = this.property(property.getName());

            if (oldProperty != null) {
                oldProperty.setValue(property.getValue());
            } else {
                this.properties.add(property);
            }
        }

        return oldProperty;
    }

    public Property setProperty(String name, String value) {
        return name != null ? this.setProperty(Property.of(name, value)) : null;
    }

    public void setProperties(Iterable<Property> properties) {
        if (properties != null) {
            for (Property property : properties) {
                this.setProperty(property);
            }
        }
    }

    public void setProperties(Property... properties) {
        this.setProperties(Arrays.asList(properties));
    }

    public boolean unsetProperty(Property property) {
        return property != null && this.properties.remove(property);
    }

    public boolean unsetProperty(String name) {
        return name != null && this.unsetProperty(this.property(name));
    }

    public void sortProperties(Comparator<? super Property> comparator) {
        this.properties.sort(comparator);
    }

    public void sortChildren(Comparator<? super Node> comparator) {
        this.children.sort(comparator);
    }

    //
    // Helper Methods
    //

    private boolean resetPrimitive() {
        boolean ok = this.isPrimitive();
        this.value = null;

        return ok;
    }

    private boolean resetTree() {
        boolean ok = this.isTree();
        this.children.clear();

        return ok;
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
        Node node = of(name);
        node.setValue(value);
        return node;
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
