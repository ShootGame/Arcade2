package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.xml.XML;
import pl.themolka.arcade.xml.XMLParser;

/**
 * @deprecated {@link KitParser}
 */
@Deprecated
public class XMLKit extends XMLParser {
    public static Kit parse(ArcadePlugin plugin, Element xml) {
        String id = xml.getAttributeValue("id");
        if (id == null) {
            return null;
        }

        Kit kit = new Kit(plugin, id);
        for (Element contentElement : xml.getChildren()) {
            KitContent<?> content = parseContent(plugin, contentElement);
            if (content != null) {
                kit.addContent(content);
            }
        }

        Attribute inheritAttribute = xml.getAttribute("inherit");
        if (inheritAttribute != null) {
            for (String inherit : ParserUtils.array(inheritAttribute.getValue())) {
                kit.addInherit(inherit);
            }
        }

        return kit;
    }

    /**
     * @deprecated Uses JDOM.
     */
    @Deprecated
    private static KitContent<?> parseContent(ArcadePlugin plugin, Element xml) {
        try {
            Parser<KitContent> parser = plugin.getParsers().forType(KitContent.class);
            return parser.parseWithName(XML.convert(xml), xml.getName()).orNull();
        } catch (ParserNotSupportedException ex) {
            return null;
        }
    }
}
