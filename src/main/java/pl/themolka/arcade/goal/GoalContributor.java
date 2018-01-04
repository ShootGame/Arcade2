package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerSnapshot;
import pl.themolka.arcade.time.Time;

import java.util.UUID;

public class GoalContributor extends GamePlayerSnapshot implements Comparable<GoalContributor> {
    private Time lastTouch;
    private int touches;

    public GoalContributor(GamePlayer source) {
        super(source);
    }

    public GoalContributor(String displayName, boolean participating, String username, UUID uuid) {
        super(displayName, participating, username, uuid);
    }

    @Override
    public int compareTo(GoalContributor o) {
        int compare = Integer.compare(this.getTouches(), o.getTouches());
        if (compare == 0) {
            return this.getUsername().compareToIgnoreCase(o.getUsername());
        }

        return compare;
    }

    public Time getLastTouchTime() {
        return this.lastTouch;
    }

    public int getTouches() {
        return this.touches;
    }

    public void incrementTouch() {
        this.setLastTouchTime(Time.now());
        this.touches++;
    }

    public void resetTouches() {
        this.setLastTouchTime(null);
        this.touches = 0;
    }

    public void setLastTouchTime(Time lastTouch) {
        this.lastTouch = lastTouch;
    }

    public void setTouches(int touches) {
        this.touches = touches;
    }
}
