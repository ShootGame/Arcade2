package pl.themolka.arcade.parser.type;

import org.bukkit.util.Vector;
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

@Produces(Vector.class)
public class VectorParser extends NodeParser<Vector>
                          implements InstallableParser {
    private Parser<Double> xParser;
    private Parser<Double> yParser;
    private Parser<Double> zParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.xParser = context.type(Double.class);
        this.yParser = context.type(Double.class);
        this.zParser = context.type(Double.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a vector");
    }

    @Override
    protected ParserResult<Vector> parseNode(Node node, String name, String value) throws ParserException {
        double x = this.xParser.parse(node.property("x")).orFail();
        double y = this.yParser.parse(node.property("y")).orFail();
        double z = this.zParser.parse(node.property("z")).orFail();
        return ParserResult.fine(node, name, value, new Vector(x, y, z));
    }
}
