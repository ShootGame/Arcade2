package pl.themolka.arcade.objective.flag;

import pl.themolka.arcade.game.Participator;

public interface IFlag {
    void tick(Tick tick);

    class Tick {
        private final long ticks;
        private final Participator owner;

        public Tick(long ticks,
                    Participator owner) {
            this.ticks = ticks;
            this.owner = owner;
        }

        public long getTicks() {
            return this.ticks;
        }

        public Participator getOwner() {
            return this.owner;
        }
    }
}
