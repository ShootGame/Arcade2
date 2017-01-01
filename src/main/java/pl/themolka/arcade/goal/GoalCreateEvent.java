package pl.themolka.arcade.goal;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;

public class GoalCreateEvent extends GoalEvent {
    public GoalCreateEvent(ArcadePlugin plugin, Game game, Goal goal) {
        super(plugin, game, goal);
    }
}
