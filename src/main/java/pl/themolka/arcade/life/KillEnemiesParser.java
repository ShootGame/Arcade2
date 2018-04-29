package pl.themolka.arcade.life;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Produces(KillEnemies.Config.class)
public class KillEnemiesParser extends ConfigParser<KillEnemies.Config>
                               implements InstallableParser {
    private Parser<Ref> ownerParser;
    private Parser<Ref> enemyParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("kill enemies objective");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.ownerParser = context.type(Ref.class);
        this.enemyParser = context.type(Ref.class);
    }

    @Override
    protected ParserResult<KillEnemies.Config> parseNode(Node node, String name, String value) throws ParserException {
        String id = this.parseId(node);
        Ref<Participator.Config<?>> owner = this.parseOwner(node);
        Set<Ref<Participator.Config<?>>> enemies = Collections.singleton(this.enemyParser.parse(node).orFail());

        return ParserResult.fine(node, name, value, new KillEnemies.Config() {
            public String id() { return id; }
            public Ref<Participator.Config<?>> owner() { return owner; }
            public Set<Ref<Participator.Config<?>>> enemies() { return enemies; }
        });
    }

    @Override
    protected ParserResult<KillEnemies.Config> parseTree(Node node, String name) throws ParserException {
        String id = this.parseId(node);
        Ref<Participator.Config<?>> owner = this.parseOwner(node);
        Set<Ref<Participator.Config<?>>> enemies = this.parseEnemies(node, name, null);

        return ParserResult.fine(node, name, new KillEnemies.Config() {
            public String id() { return id; }
            public Ref<Participator.Config<?>> owner() { return owner; }
            public Set<Ref<Participator.Config<?>>> enemies() { return enemies; }
        });
    }

    protected String parseId(Node node) throws ParserException {
        return this.parseOptionalId(node);
    }

    protected Ref<Participator.Config<?>> parseOwner(Node node) throws ParserException {
        return this.ownerParser.parse(node.property("owner", "of")).orFail();
    }

    protected Set<Ref<Participator.Config<?>>> parseEnemies(Node node, String name, String value) throws ParserException {
        Set<Ref<Participator.Config<?>>> enemies = new LinkedHashSet<>();
        for (Node enemyNode : node.children("enemy", "kill")) {
            enemies.add(this.enemyParser.parse(enemyNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(enemies)) {
            throw this.fail(node, name, value, "No enemies defined");
        }

        return enemies;
    }
}