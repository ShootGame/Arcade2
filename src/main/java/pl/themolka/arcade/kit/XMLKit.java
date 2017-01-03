package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XMLKit extends XMLParser {
    public static Kit parseKit(ArcadePlugin plugin, Element xml) {
        String id = xml.getAttributeValue("id");
        if (id == null) {
            return null;
        }

        Kit kit = new Kit(plugin, id);
        for (Element contentElement : xml.getChildren()) {
            try {
                Object content = KitContentType.parseForName(contentElement.getName(), contentElement);
                if (content != null && content instanceof KitContent<?>) {
                    kit.addContent((KitContent<?>) content);
                }
            } catch (DataConversionException ex) {
                return null;
            }
        }

        for (String inherit : parseInherit(xml)) {
            kit.addInherit(inherit.trim());
        }

        return kit;
    }

    private static List<String> parseInherit(Element xml) {
        List<String> results = new ArrayList<>();

        Attribute attribute = xml.getAttribute("inherit");
        if (attribute != null) {
            results = Arrays.asList(attribute.getValue().split(","));
        }

        return results;
    }
}
