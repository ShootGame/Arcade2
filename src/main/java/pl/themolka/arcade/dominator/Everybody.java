package pl.themolka.arcade.dominator;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

/**
 * Everybody dominates.
 */
public class Everybody extends AbstractDominator {
    @Override
    public Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input) {
        return input;
    }
}
