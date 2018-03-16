package pl.themolka.arcade.item;

import org.bukkit.enchantments.Enchantment;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(ItemEnchantment.class)
public class ItemEnchantmentParser extends NodeParser<ItemEnchantment>
                                   implements InstallableParser {
    private Parser<Enchantment> typeParser;
    private Parser<Integer> levelParser;

    @Override
    public void install(ParserContext context) {
        this.typeParser = context.type(Enchantment.class);
        this.levelParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("item (leveled) enchantment");
    }

    @Override
    protected ParserResult<ItemEnchantment> parsePrimitive(Node node, String name, String value) throws ParserException {
        Enchantment type = this.typeParser.parseWithDefinition(node, name, value).orFail();

        Property levelProperty = node.property("level", "lvl");
        int level = this.levelParser.parse(levelProperty).orDefault(1);
        if (level <= 0) {
            throw this.fail(levelProperty, "Level must be positive (greater than 0)");
        }

        return ParserResult.fine(node, name, value, new ItemEnchantment(type, level));
    }
}
