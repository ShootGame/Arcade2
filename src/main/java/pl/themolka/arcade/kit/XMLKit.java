package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.xml.XML;
import pl.themolka.arcade.xml.XMLParser;

/**
 * @deprecated {@link KitParser}
 */
@Deprecated
public class XMLKit extends XMLParser {
    public static Kit parse(Game game, Element xml) {
        ArcadePlugin plugin = game.getPlugin();

        String id = xml.getAttributeValue("id");
        if (id == null) {
            return null;
        }

        Kit kit = new Kit(plugin, id);
        for (Element contentElement : xml.getChildren()) {
            KitContent.Config<?, ?> config = parseContent(plugin, contentElement);
            if (config == null) {
                continue;
            }

            KitContent<?> content = config.create(game);
            if (content != null) {
                kit.addContent(content);
            }
        }

        if (kit.getContent().isEmpty()) {
            return null;
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
    private static KitContent.Config<?, ?> parseContent(ArcadePlugin plugin, Element xml) {
        try {
            return plugin.getParsers().forType(KitContent.Config.class)
                                      .parseWithName(XML.convert(xml), xml.getName())
                                      .orDefaultNull();
        } catch (ParserNotSupportedException | ParserException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
