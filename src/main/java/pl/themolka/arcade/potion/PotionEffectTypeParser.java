package pl.themolka.arcade.potion;

import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(PotionEffectType.class)
public class PotionEffectTypeParser extends ElementParser<PotionEffectType> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("potion effect type");
    }

    @Override
    protected Result<PotionEffectType> parseElement(Element element, String name, String value) throws ParserException {
        PotionEffectType type = PotionEffectType.getByName(this.normalizePotionEffectName(value));
        if (type == null) {
            throw this.fail(element, name, value, "Unknown potion effect type");
        }

        return Result.fine(element, name, value, type);
    }

    protected String normalizePotionEffectName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
