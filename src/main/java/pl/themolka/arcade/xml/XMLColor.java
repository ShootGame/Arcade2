package pl.themolka.arcade.xml;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLColor extends XMLParser {
    public static Color parse(Attribute xml) {
        return parse(xml, null);
    }

    public static Color parse(Attribute xml, Color def) {
        DyeColor color = XMLDyeColor.parse(xml);
        if (color != null) {
            return color.getColor();
        }

        return def;
    }

    public static Color parse(Element xml) {
        return parse(xml, null);
    }

    public static Color parse(Element xml, Color def) {
        DyeColor color = XMLDyeColor.parse(xml);
        if (color != null) {
            return color.getColor();
        }

        return def;
    }

    public static Color parse(String name) {
        return parse(name, null);
    }

    public static Color parse(String name, Color def) {
        DyeColor color = XMLDyeColor.parse(name, null);
        if (color != null) {
            return color.getColor();
        }

        return def;
    }

    //
    // Firework colors
    //

    public static Color parseFireworkColor(Attribute xml) {
        return parseFireworkColor(xml, null);
    }

    public static Color parseFireworkColor(Attribute xml, Color def) {
        DyeColor color = XMLDyeColor.parse(xml, null);
        if (color != null) {
            return color.getFireworkColor();
        }

        return def;
    }

    public static Color parseFireworkColor(Element xml) {
        return parseFireworkColor(xml, null);
    }

    public static Color parseFireworkColor(Element xml, Color def) {
        DyeColor color = XMLDyeColor.parse(xml, null);
        if (color != null) {
            return color.getFireworkColor();
        }

        return def;
    }

    public static Color parseFireworkColor(String name) {
        return parseFireworkColor(name, null);
    }

    public static Color parseFireworkColor(String name, Color def) {
        DyeColor color = XMLDyeColor.parse(name, null);
        if (color != null) {
            return color.getColor();
        }

        return def;
    }
}
