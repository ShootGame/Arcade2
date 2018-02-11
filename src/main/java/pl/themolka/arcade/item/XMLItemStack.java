package pl.themolka.arcade.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
                .flags(parseFlags(xml))
                .type(parseType(xml))
                .unbreakable(parseUnbreakable(xml));

        ItemStack item = builder.build();
        item.setItemMeta(parseItemMeta(xml, item.getItemMeta()));
        item.setDurability(parseData(xml));
        return item;
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

    private static byte parseData(Element xml) {
        Attribute attribute = xml.getAttribute(XMLMaterial.ATTRIBUTE_MATERIAL);
        if (attribute != null) {
            String[] split = attribute.getValue().split(":");
            if (split.length > 1) {
                return Byte.parseByte(split[1]);
            }
        }

        return 0;
    }

    private static List<String> parseDescription(Element xml) {
        List<String> description = new ArrayList<>();

        Element element = xml.getChild("description");
        if (element != null) {
            for (Element line : element.getChildren("line")) {
                description.add(XMLParser.parseMessage(line.getValue()));
            }
        }

        return description;
    }

    private static String parseDisplayName(Element xml) {
        Element element = xml.getChild("name");
        if (element != null) {
            return XMLParser.parseMessage(element.getValue());
        }

        return null;
    }

    private static short parseDurability(Element xml) {
        Element element = xml.getChild("durability");
        if (element != null) {
            try {
                return Short.parseShort(element.getValue());
            } catch (NumberFormatException ignored) {
            }
        }

        return 0;
    }

    private static Map<Enchantment, Integer> parseEnchantments(Element xml) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        enchantments.putAll(parseEnchantments0(xml));

        Element element = xml.getChild("enchantments");
        if (element != null) {
            enchantments.putAll(parseEnchantments0(element));
        }

        return enchantments;
    }

    private static Map<Enchantment, Integer> parseEnchantments0(Element xml) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        for (Element enchantment : xml.getChildren("enchantment")) {
            Enchantment type = Enchantment.getByName(XMLParser.parseEnumValue(enchantment.getValue()));
            int level = parseInt(enchantment.getAttributeValue("level"), 1);

            if (type != null && level > 0) {
                enchantments.put(type, level);
            }
        }

        return enchantments;
    }

    private static List<ItemFlag> parseFlags(Element xml) {
        List<ItemFlag> flags = new ArrayList<>();

        for (Element element : xml.getChildren("flag")) {
            try {
                flags.add(ItemFlag.valueOf(parseEnumValue(element.getValue())));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return flags;
    }

    private static ItemMeta parseItemMeta(Element xml, ItemMeta meta) {
        return XMLItemMeta.parse(xml, meta);
    }

    private static Material parseType(Element xml) {
        return XMLMaterial.parse(xml);
    }

    private static boolean parseUnbreakable(Element xml) {
        Element element = xml.getChild("unbreakable");
        return element != null && XMLParser.parseBoolean(element.getValue());
    }
}
