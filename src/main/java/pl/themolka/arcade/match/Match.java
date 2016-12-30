package pl.themolka.arcade.match;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;

import java.time.Duration;
import java.time.Instant;

public class Match {
    private final ArcadePlugin plugin;

    private boolean forceEnd;
    private boolean forceStart;
    private final Game game;
    private Instant startTime;
    private MatchState state = MatchState.STARTING;
    private Duration time;

    public Match(ArcadePlugin plugin, Game game) {
        this.plugin = plugin;

        this.game = game;
    }

    public boolean cannotStart() {
        return false;
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

        this.time = Duration.between(this.startTime, Instant.now());

        this.setForceEnd(force);
        this.setState(MatchState.CYCLING);
        for (GamePlayer player : this.getGame().getPlayers()) {
            player.reset();
        }
    }

    public Game getGame() {
        return this.game;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public MatchState getState() {
        return this.state;
    }

    public Duration getTime() {
        return this.time;
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

        this.startTime = Instant.now();

        this.setForceStart(force);
        this.setState(MatchState.RUNNING);
    }
}
