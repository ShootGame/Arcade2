package pl.themolka.arcade.item;

import org.bukkit.enchantments.Enchantment;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Enchantment.class)
public class EnchantmentParser extends ElementParser<Enchantment> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("enchantment type");
    }

    @Override
    protected ParserResult<Enchantment> parseElement(Element element, String name, String value) throws ParserException {
        Enchantment enchantment = Enchantment.getByName(value);
        if (enchantment == null) {
            throw this.fail(element, name, value, "Unknown enchantment type");
        }

        return ParserResult.fine(element, name, value, enchantment);
    }
}
