package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

public class Match {
    private final ArcadePlugin plugin;

    private boolean forceEnd;
    private boolean forceStart;
    private final Game game;
    private MatchState state = MatchState.STARTING;

    public Match(ArcadePlugin plugin, Game game) {
        this.plugin = plugin;

        this.game = game;
    }

    public void end(boolean force) {
        this.end(null, force);
    }

    public void end(MatchWinner winner, boolean force) {
        if (!this.getState().equals(MatchState.RUNNING)) {
            return;
        }

        MatchEndEvent endEvent = new MatchEndEvent(this.plugin, this, winner, force);
        this.plugin.getEvents().post(endEvent);

        if (endEvent.isCanceled()) {
            return;
        }

        this.setForceEnd(force);
        this.setState(MatchState.CYCLING);
        for (GamePlayer player : this.getGame().getPlayers()) {
            player.reset();
        }
    }

    public Game getGame() {
        return this.game;
    }

    public MatchState getState() {
        return this.state;
    }

    public boolean isForceEnd() {
        return this.forceEnd;
    }

    public boolean isForceStart() {
        return this.forceStart;
    }

    public void setForceEnd(boolean forceEnd) {
        this.forceEnd = forceEnd;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public void start(boolean force) {
        if (!this.getState().equals(MatchState.STARTING)) {
            return;
        }

        MatchStartEvent startEvent = new MatchStartEvent(this.plugin, this, force);
        this.plugin.getEvents().post(startEvent);

        if (startEvent.isCanceled()) {
            return;
        }

        this.setForceStart(force);
        this.setState(MatchState.RUNNING);
    }
}
