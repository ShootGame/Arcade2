package pl.themolka.arcade.dominator;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

/**
 * The only one participator dominates.
 */
public class Exclusive extends AbstractDominator {
    @Override
    public Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input) {
        return input.keySet().size() == 1 ? input : this.empty();
    }
}
