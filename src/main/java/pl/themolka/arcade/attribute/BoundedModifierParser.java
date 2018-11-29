package pl.themolka.arcade.attribute;

import org.bukkit.attribute.AttributeModifier;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(BoundedModifier.class)
public class BoundedModifierParser extends NodeParser<BoundedModifier>
                                   implements InstallableParser {
    private Parser<AttributeKey> keyParser;
    private Parser<AttributeModifier> modifierParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.keyParser = context.type(AttributeKey.class);
        this.modifierParser = context.type(AttributeModifier.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("bounded (keyed) attribute modifier");
    }

    @Override
    protected Result<BoundedModifier> parseNode(Node node, String name, String value) throws ParserException {
        AttributeKey key = this.keyParser.parse(node.property("attribute", "attribute-key", "attributekey", "attr", "key")).orFail();
        AttributeModifier modifier = this.modifierParser.parseWithDefinition(node, name, value).orFail();

        return Result.fine(node, name, value, new BoundedModifier(key, modifier));
    }
}
