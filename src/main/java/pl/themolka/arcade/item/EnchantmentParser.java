package pl.themolka.arcade.item;

import org.bukkit.enchantments.Enchantment;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(Enchantment.class)
public class EnchantmentParser extends ElementParser<Enchantment> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("enchantment type");
    }

    @Override
    protected Result<Enchantment> parseElement(Element element, String name, String value) throws ParserException {
        Enchantment enchantment = Enchantment.getByName(this.normalizeEnchantmentName(value));

        if (enchantment == null) {
            // Try NMS then
            net.minecraft.server.Enchantment nms = net.minecraft.server.Enchantment.b(this.normalizeEnchantmentName(value));
            if (nms != null) {
                Enchantment.getById(net.minecraft.server.Enchantment.getId(nms));
            }
        }

        if (enchantment == null) {
            throw this.fail(element, name, value, "Unknown enchantment type");
        }

        return Result.fine(element, name, value, enchantment);
    }

    protected String normalizeEnchantmentName(String input) {
        return EnumParser.toEnumValue(input);
    }
}
