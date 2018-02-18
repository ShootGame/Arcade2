package pl.themolka.arcade.dom;

public class DOMException extends Exception {
    private final Element element;

    public DOMException(Element element) {
        this.element = element;
    }

    public DOMException(Element element, String message) {
        super(message);
        this.element = element;
    }

    public DOMException(Element element, String message, Throwable cause) {
        super(message, cause);
        this.element = element;
    }

    public DOMException(Element element, Throwable cause) {
        super(cause);
        this.element = element;
    }

    public DOMException(Element element, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String message = this.getMessage();
        if (message != null) {
            builder.append("Error: \"").append(message).append("\"");
        } else {
            builder.append("Unknown error");
        }

        Element element = this.getElement();
        if (element.hasLocation()) {
            Cursor location = element.getLocation();
            builder.append(" at line ")
                   .append(location.getLine())
                   .append(":")
                   .append(location.getColumn());
        }

        String near = element.toShortString();
        if (near != null) {
            builder.append(" near ").append(near);
        } else {
            builder.append(".");
        }

        return builder.toString();
    }
}
