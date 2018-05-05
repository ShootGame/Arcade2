package pl.themolka.arcade.objective.point;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.Progressive;

public interface IPoint extends Progressive {
    Color getColor();

    void tick(Tick tick);

    class Tick {
        private final long tick;
        private final Multimap<Participator, GamePlayer> participators;
        private final Multimap<Participator, GamePlayer> dominators;
        private final Participator owner;

        public Tick(long tick,
                    Multimap<Participator, GamePlayer> participators,
                    Multimap<Participator, GamePlayer> dominators,
                    Participator owner) {
            this.tick = tick;
            this.participators = ImmutableMultimap.copyOf(participators);
            this.dominators = ImmutableMultimap.copyOf(dominators);
            this.owner = owner;
        }

        public long getTick() {
            return this.tick;
        }

        public Multimap<Participator, GamePlayer> getParticipators() {
            return this.participators;
        }

        public Multimap<Participator, GamePlayer> getDominators() {
            return this.dominators;
        }

        public Participator getOwner() {
            return this.owner;
        }
    }
}
