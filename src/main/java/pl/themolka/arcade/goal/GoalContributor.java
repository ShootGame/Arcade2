package pl.themolka.arcade.goal;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerSnapshot;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoalContributor extends GamePlayerSnapshot implements Comparable<GoalContributor> {
    private List<Touch> touches = new ArrayList<>();

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
        if (this.touches.isEmpty()) {
            return null;
        }

        return this.touches.get(this.touches.size() - 1).time;
    }

    public int getTouches() {
        return this.touches.size();
    }

    public void touch() {
        this.touches.add(new Touch(Time.now()));
    }

    public void resetTouches() {
        this.touches.clear();
    }

    private class Touch {
        final Time time;

        Touch(Time time) {
            this.time = time;
        }
    }
}
