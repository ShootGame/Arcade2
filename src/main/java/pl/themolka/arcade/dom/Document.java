package pl.themolka.arcade.dom;

import java.nio.file.Path;

/**
 * 500th class :D
 */
public class Document implements Content, Locatable {
    private Path path;
    private Node root;

    private Selection location;

    protected Document(Path path) {
        this.path = path;
    }

    @Override
    public boolean isSelectable() {
        return this.location != null;
    }

    @Override
    public void locate(Selection selection) {
        this.location = selection;
    }

    @Override
    public Selection select() {
        if (this.location != null) {
            return this.location;
        } else if (this.hasRoot() && this.root.isSelectable()) {
            return this.root.select();
        }

        return null;
    }

    public Path getPath() {
        return this.path;
    }

    public Node getRoot() {
        return this.root;
    }

    public boolean hasPath() {
        return this.path != null;
    }

    public boolean hasRoot() {
        return this.root != null;
    }

    public Node setRoot(Node root) {
        Node oldRoot = this.root;
        this.root = root;

        return oldRoot;
    }

    @Override
    public String toShortString() {
        if (this.hasRoot()) {
            return this.getRoot().toShortString();
        } else if (this.hasPath()) {
            return "<#document at '" + this.getPath().toString() + "'>";
        }

        return "<#document>";
    }

    @Override
    public String toString() {
        return this.hasRoot() ? this.getRoot().toString() : super.toString();
    }

    //
    // Instancing
    //

    public static Document create(Path path) {
        return new Document(path);
    }

    public static Document create(Path path, Node root) {
        Document document = create(path);
        document.setRoot(root);
        return document;
    }
}
