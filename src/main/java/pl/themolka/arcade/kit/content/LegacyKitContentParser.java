package pl.themolka.arcade.kit.content;

import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * @deprecated {@link BaseContentParser}
 */
@Deprecated
public interface LegacyKitContentParser<T> {
    T parse(Element xml) throws DataConversionException;
}
