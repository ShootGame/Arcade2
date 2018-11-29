package pl.themolka.arcade.attribute;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(AttributeKey.class)
public class AttributeKeyParser extends ElementParser<AttributeKey>
                                implements InstallableParser {
    private Parser<String> namespaceParser;
    private Parser<String> keyParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.namespaceParser = context.text();
        this.keyParser = context.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("attribute key, such as 'generic.attackDamage'");
    }

    @Override
    protected Result<AttributeKey> parseElement(Element element, String name, String value) throws ParserException {
        String[] input = value.split(".", 2);
        if (input.length != 2) {
            throw this.fail(element, name, value, "Requires a namespace and a key separated with a dot");
        }

        String namespace = this.normalizeInput(this.namespaceParser.parseWithDefinition(element, name, input[0]).orFail());
        String key = this.normalizeInput(this.keyParser.parseWithDefinition(element, name, input[1]).orFail());
        return Result.fine(element, name, value, new FixedAttributeKey(namespace, key));
    }
}
