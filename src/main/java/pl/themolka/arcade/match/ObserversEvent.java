package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.team.PlayerTeamEvent;

public class ObserversEvent extends PlayerTeamEvent {
    private final Observers observers;

    public ObserversEvent(ArcadePlugin plugin, GamePlayer player, Observers observers) {
        super(plugin, player, observers);

        this.observers = observers;
    }

    public Observers getObservers() {
        return this.observers;
    }
}
