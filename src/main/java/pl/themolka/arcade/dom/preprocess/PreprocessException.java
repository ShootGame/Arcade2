package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Content;
import pl.themolka.arcade.dom.DOMException;

public class PreprocessException extends DOMException {
    public PreprocessException(Content content) {
        super(content);
    }

    public PreprocessException(Content content, String message) {
        super(content, message);
    }

    public PreprocessException(Content content, String message, Throwable cause) {
        super(content, message, cause);
    }

    public PreprocessException(Content content, Throwable cause) {
        super(content, cause);
    }
}
