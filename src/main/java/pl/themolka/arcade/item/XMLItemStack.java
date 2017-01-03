package pl.themolka.arcade.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.xml.XMLMaterial;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLItemStack extends XMLParser {
    public static ItemStack parse(Element xml) throws DataConversionException {
        if (xml == null) {
            return null;
        }

        ItemStackBuilder builder = new ItemStackBuilder()
                .amount(parseAmount(xml))
                .description(parseDescription(xml))
                .displayName(parseDisplayName(xml))
                .durability(parseDurability(xml))
                .enchantments(parseEnchantments(xml))
                .type(parseType(xml))
                .unbreakable(parseUnbreakable(xml));

        return builder.build();
    }

    private static int parseAmount(Element xml) {
        Attribute attribute = xml.getAttribute("amount");
        if (attribute != null) {
            try {
                return attribute.getIntValue();
            } catch (DataConversionException ignored) {
            }
        }

        return 1;
    }

    private static List<String> parseDescription(Element xml) {
        List<String> description = new ArrayList<>();

        Element element = xml.getChild("description");
        if (element != null) {
            for (Element line : element.getChildren("line")) {
                description.add(XMLParser.parseMessage(line.getTextNormalize()));
            }
        }

        return description;
    }

    private static String parseDisplayName(Element xml) {
        Element element = xml.getChild("name");
        if (element != null) {
            return XMLParser.parseMessage(element.getTextNormalize());
        }

        return null;
    }

    private static short parseDurability(Element xml) {
        Element element = xml.getChild("durability");
        if (element != null) {
            try {
                return Short.parseShort(element.getTextNormalize());
            } catch (NumberFormatException ignored) {
            }
        }

        return 0;
    }

    private static Map<Enchantment, Integer> parseEnchantments(Element xml) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        Element element = xml.getChild("enchantments");
        if (element != null) {
            for (Element enchantment : element.getChildren("enchantment")) {
                Enchantment type = Enchantment.getByName(XMLParser.parseEnumValue(enchantment.getTextNormalize()));
                int level = 1;

                try {
                    Attribute levelAttribute = enchantment.getAttribute("level");
                    if (levelAttribute != null) {
                        level = levelAttribute.getIntValue();
                    }
                } catch (DataConversionException ignored) {
                }

                enchantments.put(type, level);
            }
        }

        return enchantments;
    }

    private static Material parseType(Element xml) {
        return XMLMaterial.parse(xml);
    }

    private static boolean parseUnbreakable(Element xml) {
        Element element = xml.getChild("unbreakable");
        return element != null && XMLParser.parseBoolean(element.getTextNormalize());
    }
}
