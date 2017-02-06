package pl.themolka.arcade.match;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerVisibilityFilter;

/**
 * {@link PlayerVisibilityFilter}'s implementation in {@link Match}es.
 */
public class MatchVisibilityFilter implements PlayerVisibilityFilter {
    private final Match match;

    public MatchVisibilityFilter(Match match) {
        this.match = match;
    }

    /**
     * Test if the match is running and the given <code>viewer</code> and <code>player</code> are participating.
     * @param viewer Viewer who can or cannot see the <code>player</code>.
     * @param player Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if the <code>player</code> is visible, <code>false</code> otherwise.
     */
    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer player) {
        return !this.getMatch().isRunning() || !viewer.isParticipating() || player.isParticipating();
    }

    public Match getMatch() {
        return this.match;
    }
}
