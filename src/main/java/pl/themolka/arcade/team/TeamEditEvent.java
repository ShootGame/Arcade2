package pl.themolka.arcade.team;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class TeamEditEvent extends GameEvent implements TeamHolder {
    private final Team newState;
    private final Team oldState;
    private final Reason reason;

    public TeamEditEvent(ArcadePlugin plugin, Team newState, Team oldState, Reason reason) {
        super(plugin);

        this.newState = newState;
        this.oldState = oldState;
        this.reason = reason;
    }

    @Override
    public Team getTeam() {
        return this.getNewState();
    }

    public Team getNewState() {
        return this.newState;
    }

    public Team getOldState() {
        return this.oldState;
    }

    public Reason getReason() {
        return this.reason;
    }

    public enum Reason {
        FRIENDLY_FIRE, MAX_PLAYERS, MIN_PLAYERS, PAINT, RENAME, SLOTS;
    }
}
