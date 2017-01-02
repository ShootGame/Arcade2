package pl.themolka.arcade.kit;

import org.jdom2.Element;

public interface IKitContentParser<T> {
    KitContent<T> parse(Element xml);
}
