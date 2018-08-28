package pl.themolka.arcade.life;

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

import java.util.HashSet;
import java.util.Set;

@Produces(KillEnemiesGame.Config.class)
public class KillEnemiesGameParser extends GameModuleParser<KillEnemiesGame, KillEnemiesGame.Config>
                                   implements InstallableParser {
    private Parser<KillEnemies.Config> objectiveParser;

    public KillEnemiesGameParser() {
        super(KillEnemiesGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("kill-enemies", "killenemies", "kill-enemy", "killenemy", "kill-all", "killall");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.objectiveParser = context.type(KillEnemies.Config.class);
    }

    @Override
    protected ParserResult<KillEnemiesGame.Config> parseTree(Node node, String name) throws ParserException {
        Set<KillEnemies.Config> objectives = new HashSet<>();
        for (Node objectiveNode : node.children("participator", "competitor", "team")) {
            objectives.add(this.objectiveParser.parse(objectiveNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(objectives)) {
            throw this.fail(node, name, null, "No objectives defined");
        }

        return ParserResult.fine(node, name, new KillEnemiesGame.Config() {
            public Ref<Set<KillEnemies.Config>> objectives() { return Ref.ofProvided(objectives); }
        });
    }
}
