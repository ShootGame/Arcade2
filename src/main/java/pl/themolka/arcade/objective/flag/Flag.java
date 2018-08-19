package pl.themolka.arcade.objective.flag;

import org.bukkit.ChatColor;
import org.bukkit.block.Banner;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.objective.StatableObjective;
import pl.themolka.arcade.objective.flag.state.FlagState;
import pl.themolka.arcade.objective.flag.state.FlagStateFactory;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Tickable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flag extends StatableObjective<FlagState> implements IFlag, Tickable {
    public static final Time TICK_INTERVAL = Time.TICK;

    private final Set<Capture> captures = new HashSet<>();
    private FlagItem item;
    private final FlagStateFactory stateFactory;

    protected Flag(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        for (Capture.Config capture : config.captures()) {
            this.captures.add((Capture) library.getOrDefine(game, capture));
        }

        this.stateFactory = new FlagStateFactory(this);
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
    public List<Object> getEventListeners() {
        List<Object> listeners = new ArrayList<>();
        for (Capture capture : this.captures) {
            listeners.add(capture.getTracker());
        }

        return listeners;
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
        this.tick(new Tick(tick, this.getOwner()));
    }

    public void build() {
        Banner block = null;
        this.item = new FlagItem(block, this);
    }

    public Set<Capture> getCaptures() {
        return new HashSet<>(this.captures);
    }

    public FlagItem getItem() {
        return this.item;
    }

    public FlagStateFactory getStateFactory() {
        return this.stateFactory;
    }

    //
    // IFlag
    //

    @Override
    public void tick(Tick tick) {
        this.getState().tick(tick);
    }

    public interface Config extends StatableObjective.Config<Flag> {
        String DEFAULT_NAME = "Flag";

        Set<Capture.Config> captures();

        @Override
        default Flag create(Game game, Library library) {
            return new Flag(game, library, this);
        }
    }
}
