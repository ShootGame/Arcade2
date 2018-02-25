package pl.themolka.arcade.generator;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.util.PluginInstallable;

import java.util.List;

@Produces(Generator.class)
public class GeneratorParser extends NodeParser<Generator>
                             implements InstallableParser, PluginInstallable {
    private ArcadePlugin plugin;

    private EnumParser<GeneratorType> typeParser;

    @Override
    public void installPlugin(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void install(ParserContext context) {
        this.typeParser = context.enumType(GeneratorType.class);
    }

    @Override
    public List<Object> expect() {
        return this.typeParser.expectedValues();
    }

    @Override
    protected ParserResult<Generator> parsePrimitive(Node node, String name, String value) throws ParserException {
        GeneratorType type = this.typeParser.parseWithDefinition(node, name, value).orFail();
        return ParserResult.fine(node, name, value, type.create(this.plugin, node));
    }
}
