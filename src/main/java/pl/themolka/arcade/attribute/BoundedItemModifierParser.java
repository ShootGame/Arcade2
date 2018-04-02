package pl.themolka.arcade.attribute;

import org.bukkit.attribute.ItemAttributeModifier;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

/**
 * 1K :D
 */
@Produces(BoundedItemModifier.class)
public class BoundedItemModifierParser extends NodeParser<BoundedItemModifier>
                                       implements InstallableParser {
    private Parser<AttributeKey> keyParser;
    private Parser<ItemAttributeModifier> itemModifierParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.keyParser = context.type(AttributeKey.class);
        this.itemModifierParser = context.type(ItemAttributeModifier.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("bounded (keyed) item attribute modifier");
    }

    @Override
    protected ParserResult<BoundedItemModifier> parseNode(Node node, String name, String value) throws ParserException {
        AttributeKey key = this.keyParser.parse(node.property("attribute", "attribute-key", "attributekey", "attr", "key")).orFail();
        ItemAttributeModifier itemModifier = this.itemModifierParser.parseWithDefinition(node, name, value).orFail();

        return ParserResult.fine(node, name, value, new BoundedItemModifier(key, itemModifier));
    }
}
