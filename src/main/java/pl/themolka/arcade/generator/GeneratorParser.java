package pl.themolka.arcade.generator;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.util.PluginInstallable;

import java.util.Set;

@Produces(Generator.class)
public class GeneratorParser extends NodeParser<Generator>
                             implements InstallableParser, PluginInstallable {
    private ArcadePlugin plugin;

    private Parser<GeneratorType> generatorTypeParser;

    @Override
    public void installPlugin(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void install(ParserContext context) {
        this.generatorTypeParser = context.enumType(GeneratorType.class);
    }

    @Override
    public Set<Object> expect() {
        return this.generatorTypeParser.expect();
    }

    @Override
    protected ParserResult<Generator> parsePrimitive(Node node, String name, String value) throws ParserException {
        GeneratorType type = this.generatorTypeParser.parseWithDefinition(node, name, value).orFail();
        return ParserResult.fine(node, name, value, type.create(this.plugin, node));
    }
}
