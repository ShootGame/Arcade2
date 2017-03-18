package pl.themolka.arcade.game;

/**
 * Filtering players visibility by other players.
 */
public interface PlayerVisibilityFilter {
    /**
     * The default filter which always show other players.
     */
    PlayerVisibilityFilter DEFAULT = new DefaultVisibilityFilter();

    /**
     * Check if a <code>viewer</code> can see <code>target</code>.
     * @param viewer Viewer who can or cannot see the <code>target</code> player.
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if <code>target</code> is visible, <code>false</code> otherwise.
     */
    boolean canSee(GamePlayer viewer, GamePlayer target);

    /**
     * The default implementation of {@link PlayerVisibilityFilter}, which
     * generally filters noting. The #canSee method returns <code>true</code>.
     */
    final class DefaultVisibilityFilter implements PlayerVisibilityFilter {
        /**
         * The default implementation of {@link PlayerVisibilityFilter#canSee(GamePlayer, GamePlayer)}.
         * @param viewer Viewer who can or cannot see the <code>target</code> player.
         * @param target Player who can or cannot be viewed by the <code>viewer</code>.
         * @return always <code>target</code>.
         */
        @Override
        public boolean canSee(GamePlayer viewer, GamePlayer target) {
            return true;
        }
    }
}
