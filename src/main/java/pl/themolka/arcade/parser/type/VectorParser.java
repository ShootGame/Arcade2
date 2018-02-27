package pl.themolka.arcade.parser.type;

import org.bukkit.util.Vector;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.List;

@Produces(Vector.class)
public class VectorParser extends NodeParser<Vector>
                          implements InstallableParser {
    private Parser<Double> doubleParser;

    @Override
    public void install(ParserContext context) {
        this.doubleParser = context.type(Double.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a vector");
    }

    @Override
    protected ParserResult<Vector> parseNode(Node node, String name, String value) throws ParserException {
        double x = this.doubleParser.parse(node.property("x")).orFail();
        double y = this.doubleParser.parse(node.property("y")).orFail();
        double z = this.doubleParser.parse(node.property("z")).orFail();
        return ParserResult.fine(node, name, value, new Vector(x, y, z));
    }
}
