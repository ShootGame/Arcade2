package pl.themolka.arcade.potion;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.util.Time;
import pl.themolka.arcade.xml.XMLParser;
import pl.themolka.arcade.xml.XMLTime;

public class XMLPotionEffect extends XMLParser {
    public static PotionEffect parse(Element xml) {
        PotionEffectBuilder builder = new PotionEffectBuilder()
                .ambient(parseAmbient(xml))
                .amplifier(parseAmplifier(xml))
                .color(parseColor(xml))
                .duration(parseDuration(xml))
                .particles(parseParticles(xml))
                .type(parseType(xml));

        return builder.build();
    }

    private static boolean parseAmbient(Element xml) {
        Attribute attribute = xml.getAttribute("ambient");
        return attribute != null && parseBoolean(attribute.getValue());
    }

    private static int parseAmplifier(Element xml) {
        Attribute attribute = xml.getAttribute("amplifier");
        if (attribute != null) {
            try {
                return attribute.getIntValue();
            } catch (DataConversionException ignored) {
            }
        }

        return 1;
    }

    private static Color parseColor(Element xml) {
        Attribute attribute = xml.getAttribute("color");
        if (attribute != null) {
            // TODO parse colors
        }

        return null;
    }

    private static int parseDuration(Element xml) {
        Attribute attribute = xml.getAttribute("duration");
        if (attribute != null) {
            Time time = XMLTime.parse(attribute, Time.ofSeconds(1));

            if (!time.isForever()) {
                try {
                    return Math.toIntExact(time.toTicks());
                } catch (ArithmeticException ignored) {
                }
            }

            return Integer.MAX_VALUE;
        }

        return 20;
    }

    private static boolean parseParticles(Element xml) {
        Attribute attribute = xml.getAttribute("particles");
        return attribute != null && parseBoolean(attribute.getValue());
    }

    private static PotionEffectType parseType(Element xml) {
        return PotionEffectType.getByName(parseEnumValue(xml.getTextNormalize()));
    }
}
