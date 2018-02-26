package pl.themolka.arcade.dom;

/**
 * 500th class :D
 */
public class Document implements Content, Locatable {
    private Node root;

    private Selection location;

    protected Document() {
    }

    @Override
    public boolean isSelectable() {
        return this.location != null;
    }

    @Override
    public void locate(Cursor start, Cursor end) {
        this.location = Selection.between(start, end);
    }

    @Override
    public Selection select() {
        return this.location;
    }

    public Node getRoot() {
        return this.root;
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
    public String toString() {
        return this.hasRoot() ? this.getRoot().toString() : super.toString();
    }

    //
    // Instancing
    //

    public static Document create() {
        return new Document();
    }

    public static Document create(Node root) {
        Document document = create();
        document.setRoot(root);
        return document;
    }
}
