package pl.themolka.arcade.dom;

/**
 * Representation of a comment in a DOM document.
 */
public class Comment implements Locatable, NestedContent<Content> {
    private String text;

    private Selection location;
    private Content parent;

    @Override
    public boolean detach() {
        return this.setParent(null) != null;
    }

    @Override
    public Content getParent() {
        return this.parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
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
        if (this.location != null) {
            return this.location;
        } else if (this.hasParent()) {
            Selectable parent = this.getParent();
            if (parent.isSelectable()) {
                parent.select();
            }
        }

        return null;
    }

    @Override
    public Content setParent(Content parent) {
        Content oldParent = this.parent;
        this.parent = parent;

        return oldParent;
    }

    public String getText() {
        return this.text;
    }

    public String setText(String text) {
        String oldText = this.text;
        this.text = text;

        return oldText;
    }
}
