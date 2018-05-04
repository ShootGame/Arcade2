package pl.themolka.arcade.dominator;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

/**
 * Nobody dominates.
 */
public class Nobody extends AbstractDominator {
    @Override
    public Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input) {
        return this.empty();
    }
}
