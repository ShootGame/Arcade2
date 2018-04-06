package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerSnapshot;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.session.ArcadePlayer;

public class ParticipatingMatcher extends ConfigurableMatcher<Boolean> {
    protected ParticipatingMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Boolean) {
            return this.matches((Boolean) object);
        } else if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        } else if (object instanceof GamePlayerSnapshot) {
            return this.matches((GamePlayerSnapshot) object);
        }

        return false;
    }

    public boolean matches(GamePlayer player) {
        return player != null && this.matches(player.isParticipating());
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    public boolean matches(GamePlayerSnapshot snapshot) {
        return snapshot != null && this.matches(snapshot.isParticipating());
    }

    @NestedParserName("participating")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> participatingParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.participatingParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            boolean participating = this.participatingParser.parseWithDefinition(node, name, value).orDefault(true);

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Boolean> value() { return Ref.ofProvided(participating); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<ParticipatingMatcher, Boolean> {
        @Override
        default ParticipatingMatcher create(Game game, Library library) {
            return new ParticipatingMatcher(this);
        }
    }
}
