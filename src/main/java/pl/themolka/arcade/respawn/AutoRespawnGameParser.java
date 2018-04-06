package pl.themolka.arcade.respawn;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.life.PlayerDeathEvent;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.time.Time;

@Produces(AutoRespawnGame.Config.class)
public class AutoRespawnGameParser extends GameModuleParser<AutoRespawnGame, AutoRespawnGame.Config>
                                   implements InstallableParser {
    private Parser<Ref> filterParser;
    private Parser<Time> cooldownParser;

    public AutoRespawnGameParser() {
        super(AutoRespawnGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("auto-respawn", "autorespawn");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.filterParser = context.type(Ref.class);
        this.cooldownParser = context.type(Time.class);
    }

    @Override
    protected ParserResult<AutoRespawnGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());
        Time cooldown = this.cooldownParser.parse(node.property("cooldown", "after")).orDefault(PlayerDeathEvent.DEFAULT_AUTO_RESPAWN_COOLDOWN);

        return ParserResult.fine(node, name, value, new AutoRespawnGame.Config() {
            public Ref<Filter.Config<?>> filter() { return filter; }
            public Time cooldown() { return cooldown; }
        });
    }
}
