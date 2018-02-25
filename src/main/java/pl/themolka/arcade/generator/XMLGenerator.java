package pl.themolka.arcade.generator;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Propertable;
import pl.themolka.arcade.dom.Properties;
import pl.themolka.arcade.map.MapParserException;
import pl.themolka.arcade.xml.XMLParser;

/**
 * @deprecated {@link GeneratorParser}
 */
@Deprecated
public class XMLGenerator extends XMLParser {
    public static Generator parse(Element xml, ArcadePlugin plugin) throws MapParserException {
        if (xml == null) {
            return null;
        }

        GeneratorType type = GeneratorType.forName(xml.getValue());
        if (type == null) {
            return null;
        }

        Propertable properties = new Properties();
        for (Attribute attribute : xml.getAttributes()) {
            properties.setProperty(attribute.getName(), attribute.getValue());
        }

        return (Generator) type.create(plugin, properties);
    }
}
