package pl.themolka.arcade.dominator;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public interface Dominator {
    Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input);
}
