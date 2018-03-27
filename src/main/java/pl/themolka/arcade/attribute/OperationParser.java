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

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.operationParser = context.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("attribute modifier operation, such as 'add'");
    }

    @Override
    protected ParserResult<AttributeModifier.Operation> parseElement(Element element, String name, String value) throws ParserException {
        AttributeModifier.Operation operation = this.getOperation(this.operationParser.parse(element).orFail());
        if (operation == null) {
            throw this.fail(element, name, value, "Unknown attribute modifier operation type");
        }

        return ParserResult.fine(element, name, value, operation);
    }

    protected AttributeModifier.Operation getOperation(String input) {
        switch (input.toLowerCase()) {
            case "add": return AttributeModifier.Operation.ADD_NUMBER;
            case "base": return AttributeModifier.Operation.ADD_SCALAR;
            case "multiply": return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            default: return null;
        }
    }
}
