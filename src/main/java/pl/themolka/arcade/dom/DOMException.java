package pl.themolka.arcade.dom;

public class DOMException extends Exception {
    private final Content content;

    public DOMException(Content content) {
        this.content = content;
    }

    public DOMException(Content content, String message) {
        super(message);
        this.content = content;
    }

    public DOMException(Content content, String message, Throwable cause) {
        super(message, cause);
        this.content = content;
    }

    public DOMException(Content content, Throwable cause) {
        super(cause);
        this.content = content;
    }

    public Content getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String message = this.getMessage();
        if (message != null) {
            builder.append(message);
        } else {
            builder.append("Unknown error");
        }

        if (this.content != null) {
            if (this.content.isSelectable()) {
                Selection selection = this.content.select();
                if (selection != null) {
                    builder.append(" at line ").append(selection);
                }
            }

            String near = this.content.toShortString();
            if (near != null) {
                builder.append(" in '").append(near).append("'");
            }
        }

        return builder.append(".").toString();
    }
}
