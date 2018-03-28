package pl.themolka.arcade.attribute;

import org.bukkit.attribute.AttributeModifier;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(AttributeModifier.Operation.class)
public class OperationParser extends ElementParser<AttributeModifier.Operation>
                             implements InstallableParser {
    private Parser<String> operationParser;
    private Parser<Integer> vanillaIdParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.operationParser = context.text();
        this.vanillaIdParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("attribute modifier operation, such as 'add'");
    }

    @Override
    protected ParserResult<AttributeModifier.Operation> parseElement(Element element, String name, String value) throws ParserException {
        AttributeModifier.Operation operation = this.getOperation(this.operationParser.parse(element).orDefaultNull());
        if (operation == null) {
            operation = this.parseOperationVanillaId(element, name, value);
        }

        if (operation == null) {
            throw this.fail(element, name, value, "Unknown attribute modifier operation type");
        }

        return ParserResult.fine(element, name, value, operation);
    }

    protected AttributeModifier.Operation parseOperationVanillaId(Element element, String name, String value) throws ParserException {
        Integer vanillaId = this.vanillaIdParser.parse(element).orDefaultNull();
        return isValidOperationVanillaId(vanillaId)
                ? AttributeModifier.Operation.fromOpcode(vanillaId)
                : null;
    }

    protected AttributeModifier.Operation getOperation(String input) {
        switch (input.toLowerCase()) {
            case "add": return AttributeModifier.Operation.ADD_NUMBER;
            case "base": return AttributeModifier.Operation.ADD_SCALAR;
            case "multiply": return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            default: return null;
        }
    }

    private static boolean isValidOperationVanillaId(Integer vanillaId) {
        return vanillaId != null && vanillaId >= 0 && vanillaId < AttributeModifier.Operation.values().length;
    }
}
