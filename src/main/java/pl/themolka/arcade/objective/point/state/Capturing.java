package pl.themolka.arcade.objective.point.state;

import com.google.common.collect.Multimap;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.point.Point;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Percentage;

import java.util.Objects;

public class Capturing extends PointState.Progressive {
    private final Time captureTime;
    private final Participator capturer;

    public Capturing(Point point, Time captureTime, Participator capturer) {
        super(point);

        this.captureTime = Objects.requireNonNull(captureTime, "captureTime cannot be null");
        this.capturer = Objects.requireNonNull(capturer, "capturer cannot be null");
    }

    @Override
    public Color getColor() {
        return this.capturer.getColor();
    }

    @Override
    public Time getProgressTime() {
        return this.captureTime;
    }

    @Override
    public boolean isProgressPositive() {
        return true;
    }

    @Override
    public void tick(Tick tick) {
        Point point = this.getObjective();

        Multimap<Participator, GamePlayer> dominators = tick.getDominators();
        if (!dominators.isEmpty() && !dominators.containsKey(this.capturer)) {
            // The dominator has changed.
            this.startLosing(point.getStateFactory(), this.capturer, this.getProgress());
            return;
        }

        if (dominators.keySet().size() == 1) {
            this.progress(point.getTickInterval());
        }

        if (isCaptured(this.getProgress())) {
            this.capture(point.getStateFactory(), this.capturer);
        }
    }

    public void capture(PointStateFactory factory, Participator capturer) {
        factory.newCaptured(capturer);
        factory.getPoint().completeObjective(capturer, null);
    }

    public Participator getCapturer() {
        return this.capturer;
    }

    public Losing startLosing(PointStateFactory stateFactory, Participator loser, FinitePercentage progress) {
        return stateFactory.newLosing(loser, progress);
    }

    private static boolean isCaptured(Percentage input) {
        return input.trim().isDone();
    }
}
