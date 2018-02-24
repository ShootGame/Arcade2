package pl.themolka.arcade.parser.type;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.number.FloatParser;

import java.util.Collections;
import java.util.List;

public class LocationParser extends NodeParser<Location>
                            implements InstallableParser {
    private FloatParser floatParser;
    private VectorParser vectorParser;

    @Override
    public void install(ParserContext context) {
        this.floatParser = context.of(FloatParser.class);
        this.vectorParser = context.of(VectorParser.class);
    }

    @Override
    public List<Object> expect() {
        return Collections.singletonList("a location");
    }

    @Override
    protected ParserResult<Location> parse(Node node, String name, String value) throws ParserException {
        Vector vector = this.vectorParser.parseWithDefinition(node, name, value).orFail();
        float yaw = this.floatParser.parse(node.property("yaw", "horizontal")).orDefault(180F); // north
        float pitch = this.floatParser.parse(node.property("pitch", "vertical")).orDefault(0F); // forward
        return ParserResult.fine(node, name, value, new Location((World) null, vector, yaw, pitch));
    }
}
