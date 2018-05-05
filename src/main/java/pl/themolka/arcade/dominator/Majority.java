package pl.themolka.arcade.dominator;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

import java.util.Collection;
import java.util.Map;

/**
 * Only the one participator and if has more players than other dominates.
 */
public class Majority extends AbstractDominator {
    private final Exclusive exclusive = new Exclusive();
    private final Lead lead = new Lead();

    @Override
    public Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input) {
        Map.Entry<Participator, Collection<GamePlayer>> dominator = this.findDominator(input);
        if (dominator == null) {
            return this.empty();
        }

        Participator leader = dominator.getKey();
        int playerCount = dominator.getValue().size();

        for (Map.Entry<Participator, Collection<GamePlayer>> original : input.asMap().entrySet()) {
            if (!original.getKey().equals(leader)) { // Don't iterate the current leader
                playerCount -= original.getValue().size();

                if (playerCount <= 0) {
                    break;
                }
            }
        }

        return playerCount > 0 ? this.singleton(dominator) : this.empty();
    }

    private Map.Entry<Participator, Collection<GamePlayer>> findDominator(Multimap<Participator, GamePlayer> original) {
        Multimap<Participator, GamePlayer> results = this.lead.getDominators(original);
        if (results == null) {
            return null;
        }

        results = this.exclusive.getDominators(results);
        if (results == null) {
            return null;
        }

        for (Map.Entry<Participator, Collection<GamePlayer>> entry : results.asMap().entrySet()) {
            return entry;
        }

        return null;
    }
}
