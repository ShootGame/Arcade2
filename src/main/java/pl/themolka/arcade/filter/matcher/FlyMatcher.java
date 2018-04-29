package pl.themolka.arcade.filter.matcher;

import org.bukkit.entity.Player;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.session.ArcadePlayer;

public class FlyMatcher extends ConfigurableMatcher<Boolean> {
    protected FlyMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof Boolean) {
            return this.matches((Boolean) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        } else if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        } else if (object instanceof Player) {
            return this.matches((Player) object);
        }

        return false;
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    public boolean matches(GamePlayer player) {
        return player != null && this.matches(player.getBukkit());
    }

    public boolean matches(Player bukkit) {
        return bukkit != null && this.matches(bukkit.isFlying());
    }

    @NestedParserName({"fly", "flying"})
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config> implements InstallableParser {
        private Parser<Boolean> flyParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.flyParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Config> parseNode(Node node, String name, String value) throws ParserException {
            boolean fly = this.flyParser.parseWithDefinition(node, name, value).orDefault(true);

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Boolean> value() { return Ref.ofProvided(fly); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<FlyMatcher, Boolean> {
        @Override
        default FlyMatcher create(Game game, Library library) {
            return new FlyMatcher(this);
        }
    }
}