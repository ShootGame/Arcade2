package pl.themolka.arcade.potion;

import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(PotionEffectType.class)
public class PotionEffectTypeParser extends ElementParser<PotionEffectType> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("potion effect type");
    }

    @Override
    protected ParserResult<PotionEffectType> parseElement(Element element, String name, String value) throws ParserException {
        PotionEffectType type = PotionEffectType.getByName(value);
        if (type == null) {
            throw this.fail(element, name, value, "Unknown potion effect type");
        }

        return ParserResult.fine(element, name, value, type);
    }
}
