package pl.themolka.arcade.dom;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class ImmutableNode extends Node {
    protected ImmutableNode(Namespace namespace, String name) {
        super(namespace, name);
    }

    protected ImmutableNode(Namespace namespace, String name, String value) {
        super(namespace, name, value);
    }

    @Override
    public boolean add(Collection<Node> children) {
        return false;
    }

    @Override
    public void appendChildren(Iterable<Node> append, boolean deep) {
    }

    @Override
    public void appendProperties(Iterable<Property> append) {
    }

    @Override
    public int clearChildren() {
        return 0;
    }

    @Override
    public Node clone() {
        return this;
    }

    @Override
    public boolean detach() {
        return false;
    }

    @Override
    public void locate(Cursor start, Cursor end) {
    }

    @Override
    public void locate(Selection selection) {
    }

    @Override
    public boolean remove(Iterable<Node> children) {
        return false;
    }

    @Override
    public boolean removeByName(Iterable<String> children) {
        return false;
    }

    @Override
    public String setName(String name) {
        return null;
    }

    @Override
    public Node setParent(Node parent) {
        return null;
    }

    @Override
    public Property setProperty(Property property) {
        return null;
    }

    @Override
    public void setProperties(Iterable<Property> properties) {
    }

    @Override
    public String setValue(String value) {
        return null;
    }

    @Override
    public void sortChildren(Comparator<? super Node> comparator) {
    }

    @Override
    public void sortProperties(Comparator<? super Property> comparator) {
    }

    @Override
    public int unsetProperties() {
        return 0;
    }

    @Override
    public boolean unsetProperty(Property property) {
        return false;
    }

    @Override
    public boolean unsetProperty(String name) {
        return false;
    }

    @Override
    public void append(Iterable<Node> append, boolean deep, boolean properties) {
    }

    @Override
    public void append(boolean deep, boolean properties, Node... append) {
    }

    @Override
    boolean resetPrimitive() {
        return false;
    }

    @Override
    boolean resetTree() {
        return false;
    }

    //
    // Instancing
    //

    public static ImmutableNode of(Namespace namespace, String name) {
        return new ImmutableNode(namespace, name);
    }

    public static ImmutableNode of(Namespace namespace, String name, List<Property> properties) {
        ImmutableNode node = of(namespace, name);
        node.setProperties(properties);
        return node;
    }

    public static ImmutableNode ofPrimitive(Namespace namespace, String name, String value) {
        return new ImmutableNode(namespace, name, value);
    }

    public static ImmutableNode ofPrimitive(Namespace namespace, String name, List<Property> properties, String value) {
        ImmutableNode node = ofPrimitive(namespace, name, value);
        node.setProperties(properties);
        return node;
    }

    public static ImmutableNode ofChildren(Namespace namespace, String name, List<Node> children) {
        ImmutableNode node = of(namespace, name);
        node.add(children);
        return node;
    }

    public static ImmutableNode ofChildren(Namespace namespace, String name, List<Property> properties, List<Node> children) {
        ImmutableNode node = ofChildren(namespace, name, children);
        node.setProperties(properties);
        return node;
    }
}
