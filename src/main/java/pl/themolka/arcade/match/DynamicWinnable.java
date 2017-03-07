package pl.themolka.arcade.match;

import java.util.List;

/**
 * An interface accessing modules to fetch current - dynamically
 * {@link MatchWinner}s at any time.
 */
public interface DynamicWinnable {
    /**
     * An instance of a {@link DrawMatchWinner} class handled when
     * the are many {@link MatchWinner}s at this time.
     * @return {@link DrawMatchWinner} at this time,
     * <code>null</code> if no one is winning.
     */
    DrawMatchWinner getDrawWinner();

    /**
     * Current, not cached {@link MatchWinner} in this {@link Match}.
     * @return current {@link MatchWinner} if one is winning,
     * <code>null</code> if not, {@link DrawMatchWinner} if many.
     */
    default MatchWinner getDynamicWinner() {
        List<MatchWinner> winners = this.getDynamicWinners();
        if (winners == null) {
            // no one is winning
            return null;
        } else if (winners.size() == 1) {
            // there is only a one winner, so we can return it
            return winners.get(0);
        } else {
            // there are many winner, return a draw instead
            return this.getDrawWinner();
        }
    }

    /**
     * Current list of {@link MatchWinner}s in this {@link Match}.
     * @return {@link List} of current {@link MatchWinner}s.
     * <code>null</code> if no one is currently winning.
     */
    List<MatchWinner> getDynamicWinners();
}
