package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Element;

public class PreprocessNotSupportedException extends PreprocessException {
    public PreprocessNotSupportedException(Element element) {
        super(element);
    }

    public PreprocessNotSupportedException(Element element, String message) {
        super(element, message);
    }

    public PreprocessNotSupportedException(Element element, String message, Throwable cause) {
        super(element, message, cause);
    }

    public PreprocessNotSupportedException(Element element, Throwable cause) {
        super(element, cause);
    }
}
