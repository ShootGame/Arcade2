package pl.themolka.arcade.dominator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

import java.util.Collection;
import java.util.Map;

/**
 * Participator(s) with most {@link GamePlayer}s dominates.
 */
public class Lead extends AbstractDominator {
    @Override
    public Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input) {
        Multimap<Participator, GamePlayer> results = ArrayListMultimap.create();
        for (Map.Entry<Participator, Collection<GamePlayer>> participator : input.asMap().entrySet()) {
            Collection<GamePlayer> players = participator.getValue();

            int playerCount = players.size();
            int dominatorCount = 0;

            for (Map.Entry<Participator, Collection<GamePlayer>> previous : results.asMap().entrySet()) {
                // All participators in this map have same player counts - break the loop.
                dominatorCount = previous.getValue().size();
                break;
            }

            if (playerCount < dominatorCount) {
                // Not enough participators, continue
                continue;
            } else if (playerCount > dominatorCount) {
                // Clear the map so we have just this record in it.
                results.clear();
            }

            // Record the current participator to the map.
            results.putAll(participator.getKey(), players);
        }

        return results;
    }
}
