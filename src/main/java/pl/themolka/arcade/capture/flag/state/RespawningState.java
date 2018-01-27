package pl.themolka.arcade.capture.flag.state;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Location;
import pl.themolka.arcade.capture.flag.Flag;
import pl.themolka.arcade.capture.flag.FlagRespawnedEvent;
import pl.themolka.arcade.capture.flag.FlagSpawn;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

public class RespawningState extends FlagState.Progress implements FlagState.VirtualFlag {
    public static final double RESPAWNED = Progress.DONE;

    private final FlagSpawn target;
    private final Time time;

    public RespawningState(Flag flag, FlagSpawn target, Time time) {
        super(flag);

        this.target = target;
        this.time = time;
    }

    @Override
    public FlagState copy() {
        return new RespawningState(this.flag, this.target, this.time);
    }

    @Override
    public Time getProgressTime() {
        return this.getTime();
    }

    @Override
    public void heartbeat(long ticks, Match match, GoalHolder owner) {
        this.progress();

        if (this.getProgress() >= RESPAWNED) {
            Location location = this.target.nextLocationOrDefault(100);
            if (location == null) {
                // Try in the next heartbeat then.
                return;
            }

            SpawnedState spawnedState = this.flag.createSpawnedState(location, this.target);

            FlagRespawnedEvent event = new FlagRespawnedEvent(this.game.getPlugin(), this.flag, this, spawnedState);
            this.game.getPlugin().getEventBus().publish(event);

            if (event.isCanceled()) {
                return;
            }

            this.game.getMatch().sendGoalMessage(this.flag.getRespawnMessage());
            this.flag.setState(event.getNewState());
        }
    }

    @Override
    public boolean isProgressPositive() {
        return true;
    }

    public FlagSpawn getTarget() {
        return this.target;
    }

    public Time getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("flag", this.flag)
                .append("target", this.target)
                .append("time", this.time)
                .build();
    }
}
