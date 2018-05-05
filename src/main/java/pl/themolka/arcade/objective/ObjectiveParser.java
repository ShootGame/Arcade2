package pl.themolka.arcade.objective;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(Objective.Config.class)
public class ObjectiveParser extends ConfigParser<Objective.Config<?>>
                             implements InstallableParser {
    private NestedParserMap<ObjectiveManifest.ObjectiveParser<?>> parsers;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);

        this.parsers = new NestedParserMap<>(context);
        for (ObjectiveManifest manifest : ObjectiveManifest.manifests) {
            ObjectiveManifest.ObjectiveParser<?> parser = manifest.defineParser(context);
            if (parser != null) {
                this.parsers.scanDedicatedClass(parser.getClass());
            }
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("objective");
    }

    @Override
    protected ParserResult<Objective.Config<?>> parseNode(Node node, String name, String value) throws ParserException {
        ObjectiveManifest.ObjectiveParser<?> parser = this.parsers.parse(name);
        if (parser == null) {
            throw this.fail(node, name, value, "Unknown objective type");
        }

        return ParserResult.fine(node, name, value, parser.parseWithDefinition(node, name, value).orFail());
    }
}
