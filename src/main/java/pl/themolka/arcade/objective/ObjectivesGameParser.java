package pl.themolka.arcade.objective;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(ObjectivesGame.Config.class)
public class ObjectivesGameParser extends GameModuleParser<ObjectivesGame, ObjectivesGame.Config>
                                  implements InstallableParser {
    private Parser<Objective.Config> objectiveParser;

    public ObjectivesGameParser() {
        super(ObjectivesGame.class);
    }

    @Override
    public Node define(Node source) {
        for (ObjectiveManifest manifest : ObjectiveManifest.manifests) {
            Node definition = manifest.define(source);
            if (definition != null) {
                return definition;
            }
        }

        return null;
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.objectiveParser = context.type(Objective.Config.class);
    }

    @Override
    protected ParserResult<ObjectivesGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Set<Objective.Config<?>> objectives = new LinkedHashSet<>();
        for (Node objectiveNode : node.children()) {
            objectives.add(this.objectiveParser.parse(objectiveNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(objectives)) {
            throw this.fail(node, name, value, "No game objectives defined");
        }

        return ParserResult.fine(node, name, value, new ObjectivesGame.Config() {
            public Ref<Set<Objective.Config<?>>> objectives() { return Ref.ofProvided(objectives); }
        });
    }
}
