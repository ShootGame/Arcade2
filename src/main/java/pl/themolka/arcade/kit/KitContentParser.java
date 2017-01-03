package pl.themolka.arcade.kit;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

public interface KitContentParser<T> {
    T parse(Element xml) throws DataConversionException;
}