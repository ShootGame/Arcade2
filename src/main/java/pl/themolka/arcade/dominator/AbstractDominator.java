package pl.themolka.arcade.dominator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractDominator implements Dominator {
    protected Multimap<Participator, GamePlayer> empty() {
        return ImmutableMultimap.of();
    }

    protected Multimap<Participator, GamePlayer> singleton(Map.Entry<Participator, Collection<GamePlayer>> dominator) {
        return this.singleton(dominator.getKey(), dominator.getValue());
    }

    protected Multimap<Participator, GamePlayer> singleton(Participator participator, Collection<GamePlayer> players) {
        Multimap<Participator, GamePlayer> map = ArrayListMultimap.create();
        map.putAll(participator, players);
        return ImmutableMultimap.copyOf(map);
    }
}
