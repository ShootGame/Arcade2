package pl.themolka.arcade.game;

import pl.themolka.arcade.session.ArcadePlayer;

public interface ParticipatorResolver {
    ParticipatorResolver NULL = new NullParticipatorResolver();

    default Participator resolve(ArcadePlayer player) {
        return this.resolve(player.getGamePlayer());
    }

    Participator resolve(GamePlayer player);

    interface Injector {
        void injectParticipatorResolver(ParticipatorResolver participatorResolver);
    }
}

class NullParticipatorResolver implements ParticipatorResolver {
    @Override
    public Participator resolve(ArcadePlayer player) {
        return null;
    }

    @Override
    public Participator resolve(GamePlayer player) {
        return null;
    }
}
