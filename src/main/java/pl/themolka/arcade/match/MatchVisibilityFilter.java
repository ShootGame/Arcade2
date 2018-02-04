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
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if the <code>player</code> is visible, <code>false</code> otherwise.
     */
    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer target) {
        // Viewers not in game (observers, or when the match is not
        // in the running state) always see other players. Otherwise
        // participators can only see other participators.

        return !this.isInGame(viewer) || target.isParticipating();
    }

    public Match getMatch() {
        return this.match;
    }

    /**
     * Check if the given viewer is in game. This method simply checks if
     * the current match is running and the player is participating to it.
     */
    private boolean isInGame(GamePlayer player) {
        return this.match.isRunning() && player.isParticipating();
    }
}
