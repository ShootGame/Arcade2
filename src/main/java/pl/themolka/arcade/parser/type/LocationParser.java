package pl.themolka.arcade.parser.type;

import org.bukkit.Location;
import org.bukkit.World;
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

@Produces(Location.class)
public class LocationParser extends NodeParser<Location>
                            implements InstallableParser {
    private Parser<Float> floatParser;
    private Parser<Vector> vectorParser;

    @Override
    public void install(ParserContext context) {
        this.floatParser = context.type(Float.class);
        this.vectorParser = context.type(Vector.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a location");
    }

    @Override
    protected ParserResult<Location> parseNode(Node node, String name, String value) throws ParserException {
        Vector vector = this.vectorParser.parseWithDefinition(node, name, value).orFail();
        float yaw = this.floatParser.parse(node.property("yaw", "horizontal")).orDefault(180F); // north
        float pitch = this.floatParser.parse(node.property("pitch", "vertical")).orDefault(0F); // forward
        return ParserResult.fine(node, name, value, new Location((World) null, vector, yaw, pitch));
    }
}
