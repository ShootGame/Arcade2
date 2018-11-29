package pl.themolka.arcade.item;

import org.bukkit.enchantments.EnchantmentOffer;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.PlayerLevel;

import java.util.Collections;
import java.util.Set;

@Produces(EnchantmentOffer.class)
public class EnchantmentOfferParser extends NodeParser<EnchantmentOffer>
                                    implements InstallableParser {
    private Parser<ItemEnchantment> enchantmentParser;
    private Parser<PlayerLevel> costParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.enchantmentParser = context.type(ItemEnchantment.class);
        this.costParser = context.type(PlayerLevel.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("enchantment offer");
    }

    @Override
    protected Result<EnchantmentOffer> parseNode(Node node, String name, String value) throws ParserException {
        ItemEnchantment enchantment = this.enchantmentParser.parseWithDefinition(node, name, value).orFail();

        int cost = this.costParser.parse(node.property("cost")).orFail().getLevel();
        if (cost <= 0) {
            throw this.fail(node, name, value, "Cost must be positive (greater than 0)");
        }

        EnchantmentOffer offer = new EnchantmentOffer(enchantment.getType(), enchantment.getLevel(), cost);
        return Result.fine(node, name, value, offer);
    }
}
