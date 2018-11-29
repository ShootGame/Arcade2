package pl.themolka.arcade.environment;

import pl.themolka.arcade.dom.DOMException;
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

@Produces(Environment.class)
public class EnvironmentParser extends NodeParser<Environment>
                               implements InstallableParser {
    private Parser<EnvironmentType> environmentTypeParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.environmentTypeParser = context.type(EnvironmentType.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("environment");
    }

    @Override
    protected Result<Environment> parseNode(Node node, String name, String value) throws ParserException {
        EnvironmentType type = this.environmentTypeParser.parse(node.property("type", "of")).orDefault(Environment.DEFAULT_TYPE);
        try {
            return Result.fine(node, name, value, type.buildEnvironment(node));
        } catch (DOMException ex) {
            throw this.fail(node, name, value, "Could not build environment", ex);
        }
    }
}
