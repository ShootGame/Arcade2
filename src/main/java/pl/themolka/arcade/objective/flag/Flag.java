package pl.themolka.arcade.objective.flag;

import org.bukkit.ChatColor;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.objective.StatableObjective;
import pl.themolka.arcade.objective.flag.state.FlagState;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Tickable;

public class Flag extends StatableObjective<FlagState> implements IFlag, Tickable {
    public static final Time TICK_INTERVAL = Time.TICK;

    protected Flag(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);
    }

    @Override
    public void completeObjective(Participator completer, GamePlayer player) {
        this.getGame().sendGoalMessage(this.describeOwner() + this.describeObjective() +
                ChatColor.YELLOW + " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                player.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + ".");
        super.completeObjective(completer, player);
    }

    @Override
    public FlagState defineInitialState() {
        return null;
    }

    @Override
    public String getDefaultName() {
        return Config.DEFAULT_NAME;
    }

    @Override
    public Time getTickInterval() {
        return TICK_INTERVAL;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByEveryone();
    }

    @Override
    public void onTick(long tick) {
    }

    public interface Config extends StatableObjective.Config<Flag> {
        String DEFAULT_NAME = "Flag";

        @Override
        default Flag create(Game game, Library library) {
            return new Flag(game, library, this);
        }
    }
}
