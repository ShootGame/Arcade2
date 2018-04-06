package pl.themolka.arcade.spawn;

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

import java.util.ArrayList;
import java.util.List;

@Produces(SpawnsGame.Config.class)
public class SpawnsGameParser extends GameModuleParser<SpawnsGame, SpawnsGame.Config>
                              implements InstallableParser {
    private Parser<Spawn.Config> spawnParser;

    public SpawnsGameParser() {
        super(SpawnsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("spawns", "spawn");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.spawnParser = context.type(Spawn.Config.class);
    }

    @Override
    protected ParserResult<SpawnsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        List<Spawn.Config<?>> spawns = new ArrayList<>();
        for (Node spawnNode : node.children()) {
            spawns.add(this.spawnParser.parse(spawnNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(spawns)) {
            throw this.fail(node, name, value, "No spawns defined");
        }

        return ParserResult.fine(node, name, value, new SpawnsGame.Config() {
            public Ref<List<Spawn.Config<?>>> spawns() { return Ref.ofProvided(spawns); }
        });
    }
}
