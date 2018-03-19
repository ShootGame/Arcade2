package pl.themolka.arcade.parser;

public class ParserNotSupportedException extends Exception {
    public ParserNotSupportedException() {
        super();
    }

    public ParserNotSupportedException(String message) {
        super(message);
    }

    public ParserNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserNotSupportedException(Throwable cause) {
        super(cause);
    }
}
