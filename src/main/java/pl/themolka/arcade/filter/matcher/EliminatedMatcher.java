package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.life.LivesGame;
import pl.themolka.arcade.life.LivesModule;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadePlayer;

public class EliminatedMatcher extends Matcher<GamePlayer> {
    protected EliminatedMatcher() {
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        }

        return false;
    }

    @Override
    public boolean matches(GamePlayer player) {
        if (player != null) {
            LivesGame module = (LivesGame) player.getGame().getModule(LivesModule.class);
            if (module != null) {
                return module.isEliminated(player);
            }
        }

        return false;
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    @NestedParserName("eliminated")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config> {
        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            return Result.fine(node, name, value, new Config() {});
        }
    }

    public interface Config extends Matcher.Config<EliminatedMatcher> {
        @Override
        default EliminatedMatcher create(Game game, Library library) {
            return new EliminatedMatcher();
        }
    }
}
