package pl.themolka.arcade.map;

public class MapParserException extends Exception {
    public MapParserException() {
        super();
    }

    public MapParserException(String message) {
        super(message);
    }

    public MapParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapParserException(Throwable cause) {
        super(cause);
    }
}
