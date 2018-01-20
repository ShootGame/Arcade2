package pl.themolka.arcade.capture.flag;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.flag.state.CarryingState;
import pl.themolka.arcade.capture.flag.state.DroppedState;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.capture.flag.state.RespawningState;
import pl.themolka.arcade.capture.flag.state.SpawnedState;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.match.Match;
import pl.themolka.arcade.time.Time;

import java.util.ArrayList;

public class Flag extends Capturable {
    public static final String DEFAULT_GOAL_NAME = "Flag";

    public static final int NOT_OBJECTIVE = 0;
    public static final Time HEARTBEAT_INTERVAL = Time.ofTicks(1);

    private FlagCapture capture;
    private boolean flagTouched = false;
    private final FlagState initialState;
    private final FlagItem item;
    private int objective = NOT_OBJECTIVE;
    private int objectiveResult = 0;
    private FlagSpawn spawn;
    private FlagState state;

    public Flag(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Flag(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);

        this.initialState = new InitialState(this);
        this.item = new FlagItem();

        // Set the current state to a copy of the initial state.
        this.state = this.getInitialState().copy();
    }

    @Override
    public void capture(GoalHolder completer, GamePlayer player) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        String result = "";
        if (this.isObjective()) {
            result = " (" + ChatColor.GOLD + ChatColor.BOLD + this.getObjectiveResult() +
                    ChatColor.RESET + ChatColor.YELLOW + " of " + this.getObjective() + ")";
        }

        String message = owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                player.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + result + ".";

        this.game.getMatch().sendGoalMessage(message);
        this.objectiveResult++;

        if (!this.isObjective() || this.objectiveResult >= this.objective){
            this.setCompleted(completer, true);
        }
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public String getGoalInteractMessage(String interact) {
        return null;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return super.isCompletableBy(completer);
    }

    @Override
    public boolean isUntouched() {
        return !this.flagTouched;
    }

    @Override
    public boolean registerGoal() {
        return this.isObjective();
    }

    @Override
    public void resetCapturable() {
        this.flagTouched = false;
        this.state = this.getInitialState().copy();
    }

    public DroppedState createDroppedState(GamePlayer dropper, Location location) {
        return new DroppedState(this, dropper, location);
    }

    public SpawnedState createSpawnedState(Location location, FlagSpawn spawn) {
        return new SpawnedState(this, location, spawn);
    }

    public FlagCapture getCapture() {
        return this.capture;
    }

    public FlagState getInitialState() {
        return this.initialState;
    }

    public FlagItem getItem() {
        return this.item;
    }

    public int getObjective() {
        return this.objective;
    }

    public int getObjectiveResult() {
        return this.objectiveResult;
    }

    public FlagSpawn getSpawn() {
        return this.spawn;
    }

    public FlagState getState() {
        return this.state;
    }

    public void heartbeat(long ticks) {
        this.getState().heartbeat(ticks, this.game.getMatch(), this.getOwner());
    }

    public boolean isObjective() {
        return this.objective > NOT_OBJECTIVE;
    }

    public void removeFlagItems(GamePlayer player) {
        PlayerInventory inventory = player.getBukkit().getInventory();
        for (ItemStack item : new ArrayList<>(inventory.contents())) {
            if (this.item.isSimilar(item)) {
                inventory.remove(item);
            }
        }
    }

    public void setCapture(FlagCapture capture) {
        this.capture = capture;
    }

    public void setObjective(int objective) {
        this.objective = objective;
    }

    public void setObjectiveResult(int objectiveResult) {
        this.objectiveResult = objectiveResult;
    }

    public void setSpawn(FlagSpawn spawn) {
        this.spawn = spawn;
    }

    public void setState(FlagState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("owner", this.getOwner())
                .append("captured", this.isCaptured())
                .append("capturedBy", this.getCapturedBy())
                .append("id", this.getId())
                .append("name", this.getName())
                .append("flagTouched", this.flagTouched)
                .append("objective", this.objective)
                .append("objectiveResult", this.objectiveResult)
                .build();
    }

    //
    // Starting New States
    //

    public FlagState startCarryingState(CarryingState state) {
        return this.startState(new FlagCarryingEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    public FlagState startRespawningState(RespawningState state) {
        return this.startState(new FlagRespawningEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    private FlagState startState(FlagStateEvent event) {
        this.game.getPlugin().getEventBus().publish(event);
        if (event instanceof Cancelable && ((Cancelable) event).isCanceled()) {
            return null;
        }

        FlagState newState = event.getNewState();
        this.setState(newState);
        return newState;
    }

    //
    // Listeners
    //

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GamePlayer player = this.game.getGame().getPlayer(event.getEntity());
        if (player != null) {
            this.removeFlagItems(player);
        }
    }

    private class InitialState extends FlagState {
        public InitialState(Flag flag) {
            super(flag);
        }

        @Override
        public FlagState copy() {
            return new InitialState(this.flag);
        }

        @Override
        public void heartbeat(long ticks, Match match, GoalHolder owner) {
            super.heartbeat(ticks, match, owner);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, TO_STRING_STYLE)
                    .append("flag", this.flag)
                    .build();
        }
    }
}
