package pl.themolka.arcade.dominator;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public enum DefaultDominators implements Dominator {
    EVERYBODY(new Everybody()),
    EXCLUSIVE(new Exclusive()),
    LEAD(new Lead()),
    MAJORITY(new Majority()),
    NOBODY(new Nobody()),
    ;

    private final Dominator target;

    DefaultDominators(Dominator target) {
        this.target = target;
    }

    @Override
    public Multimap<Participator, GamePlayer> getDominators(Multimap<Participator, GamePlayer> input) {
        return this.target.getDominators(input);
    }

    public Dominator getDominator() {
        return this.target;
    }

    public static Dominator getDefault() {
        return LEAD.target;
    }
}
