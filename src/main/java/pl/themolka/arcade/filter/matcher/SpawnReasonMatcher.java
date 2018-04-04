package pl.themolka.arcade.filter.matcher;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

public class SpawnReasonMatcher extends ConfigurableMatcher<SpawnReason> {
    protected SpawnReasonMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof SpawnReason) {
            return this.matches((SpawnReason) object);
        } else if (object instanceof CreatureSpawnEvent) {
            return this.matches((CreatureSpawnEvent) object);
        }

        return false;
    }

    public boolean matches(CreatureSpawnEvent event) {
        return event != null && this.matches(event.getSpawnReason());
    }

    @NestedParserName({"spawn-reason", "spawnreason"})
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<SpawnReason> reasonParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.reasonParser = context.type(SpawnReason.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            SpawnReason reason = this.reasonParser.parseWithDefinition(node, name, value).orFail();

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<SpawnReason> value() { return Ref.ofProvided(reason); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<SpawnReasonMatcher, SpawnReason> {
        @Override
        default SpawnReasonMatcher create(Game game) {
            return new SpawnReasonMatcher(this);
        }
    }
}
