package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Element;

public class ParserException extends DOMException {
    public ParserException(Element element) {
        super(element);
    }

    public ParserException(Element element, String message) {
        super(element, message);
    }

    public ParserException(Element element, String message, Throwable cause) {
        super(element, message, cause);
    }

    public ParserException(Element element, Throwable cause) {
        super(element, cause);
    }

    public ParserException(Element element, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(element, message, cause, enableSuppression, writableStackTrace);
    }
}
