package pl.themolka.arcade.dom;

/**
 * 500th class :D
 */
public class Document {
    private Node root;

    private Document() {
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
