package pl.themolka.arcade.capture.flag;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import pl.themolka.arcade.ArcadePlugin;
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
import java.util.List;
import java.util.Random;

public class Flag extends Capturable {
    public static final String DEFAULT_GOAL_NAME = "Flag";

    public static final int NOT_OBJECTIVE = 0;
    public static final Time HEARTBEAT_INTERVAL = Time.ofTicks(1);

    private final List<FlagCapture> captures = new ArrayList<>();
    private final FlagState initialState;
    private final FlagItem item;
    private int objective = NOT_OBJECTIVE;
    private int objectiveResult = 0;
    private final List<FlagSpawn> spawns = new ArrayList<>();
    private FlagState state;

    public Flag(CaptureGame game, String id) {
        this(game, null, id);
    }

    public Flag(CaptureGame game, GoalHolder owner, String id) {
        super(game, owner, id);

        this.initialState = new InitialState(this);
        this.item = new FlagItem(this);

        // Set the current state to a copy of the initial state.
        this.state = this.getInitialState().copy();
    }

    @Override
    public void capture(GoalHolder completer, GamePlayer player) {
        this.game.getMatch().sendGoalMessage(this.getCaptureMessage(player));
        this.objectiveResult++;

        if (!this.isObjective() || this.objectiveResult >= this.objective){
            this.setCompleted(completer);
        }
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_GOAL_NAME;
    }

    @Override
    public boolean registerGoal() {
        return this.isObjective();
    }

    @Override
    public void resetCapturable() {
        this.state = this.getInitialState().copy();
    }

    public boolean addCapture(FlagCapture capture) {
        return this.captures.add(capture);
    }

    public boolean addSpawn(FlagSpawn spawn) {
        return this.spawns.add(spawn);
    }

    public DroppedState createDroppedState(GamePlayer dropper, Location location) {
        return new DroppedState(this, dropper, location);
    }

    public SpawnedState createSpawnedState(Location location, FlagSpawn spawn) {
        return new SpawnedState(this, location, spawn);
    }

    public List<FlagCapture> getCaptures() {
        return new ArrayList<>(this.captures);
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

    public List<FlagSpawn> getSpawns() {
        return new ArrayList<>(this.spawns);
    }

    public FlagState getState() {
        return this.state;
    }

    public boolean hasCaptures() {
        return !this.captures.isEmpty();
    }

    public void heartbeat(long ticks) {
        this.getState().heartbeat(ticks, this.game.getMatch(), this.getOwner());
    }

    public boolean isObjective() {
        return this.objective > NOT_OBJECTIVE;
    }

    public boolean removeCapture(FlagCapture capture) {
        return this.captures.remove(capture);
    }

    public boolean removeSpawn(FlagSpawn spawn) {
        return this.spawns.add(spawn);
    }

    public void setObjective(int objective) {
        this.objective = objective;
    }

    public void setObjectiveResult(int objectiveResult) {
        this.objectiveResult = objectiveResult;
    }

    public void setState(FlagState state) {
        String from = this.state != null ? this.state.getStateName() : "unknown";
        String to = state != null ? state.getStateName() : "unknown";

        this.game.getLogger().info(this.getName() + " is transforming from " + from + " to " + to + "...");
        this.state = state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, TO_STRING_STYLE)
                .append("owner", this.getOwner())
                .append("completed", this.isCompleted())
                .append("completedBy", this.getCompletedBy())
                .append("id", this.getId())
                .append("name", this.getName())
                .append("touched", this.isTouched())
                .append("objective", this.objective)
                .append("objectiveResult", this.objectiveResult)
                .build();
    }

    //
    // Starting New States
    //

    public FlagState startCarryingState(CarryingState state) {
        return this.startState(new FlagPickedUpEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    public FlagState startCarryingState(GamePlayer carrier, Location source) {
        return this.startCarryingState(new CarryingState(this, carrier, source));
    }

    public FlagState startRespawningState(RespawningState state) {
        state.setProgress(FlagState.Progress.ZERO); // ZERO
        return this.startState(new FlagRespawningEvent(this.game.getPlugin(), this, this.getState(), state));
    }

    public FlagState startRespawningState(FlagSpawn target, Time time) {
        return this.startRespawningState(new RespawningState(this, target, time));
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
    // Messages
    //

    public String getCaptureMessage(GamePlayer capturer) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        String result = "";
        if (this.isObjective()) {
            result = " (" + ChatColor.GOLD + ChatColor.BOLD + this.getObjectiveResult() +
                    ChatColor.RESET + ChatColor.YELLOW + " of " + this.getObjective() + ")";
        }

        return owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has been captured by " + ChatColor.GOLD + ChatColor.BOLD +
                capturer.getDisplayName() + ChatColor.RESET + ChatColor.YELLOW + result + ".";
    }

    public String getDropMessage(GamePlayer dropper) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        return ChatColor.GOLD.toString() + ChatColor.BOLD + dropper.getDisplayName() + ChatColor.RESET +
                ChatColor.YELLOW + " has dropped " + owner + ChatColor.GOLD + ChatColor.BOLD +
                ChatColor.ITALIC + this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }

    public String getPickupMessage(GamePlayer picker) {
        String owner = "";
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        return ChatColor.GOLD.toString() + ChatColor.BOLD + picker.getDisplayName() + ChatColor.RESET +
                ChatColor.YELLOW + " has picked up " + owner + ChatColor.GOLD + ChatColor.BOLD +
                ChatColor.ITALIC + this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW + ".";
    }

    public String getRespawnMessage() {
        String owner = null;
        if (this.hasOwner()) {
            owner = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.YELLOW + "'s ";
        }

        return owner + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getColoredName() + ChatColor.RESET + ChatColor.YELLOW +
                " has respawned.";
    }

    private class InitialState extends FlagState {
        final Random random = new Random();

        InitialState(Flag flag) {
            super(flag);
        }

        @Override
        public FlagState copy() {
            return new InitialState(this.flag);
        }

        @Override
        public void heartbeat(long ticks, Match match, GoalHolder owner) {
            List<FlagSpawn> spawns = this.flag.getSpawns();
            if (spawns.isEmpty()) {
                return;
            }

            FlagSpawn spawn = spawns.get(this.random.nextInt(spawns.size()));
            Location location = spawn.nextLocationOrDefault(100);

            if (location != null) {
                this.flag.setState(new SpawnedState(this.flag, location, spawn));

                ArcadePlugin plugin = this.flag.getGame().getPlugin();
                plugin.getEventBus().publish(new FlagInitEvent(plugin, this.flag, spawn, location));
            }
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, TO_STRING_STYLE)
                    .append("flag", this.flag)
                    .build();
        }
    }

    public static class FlagInitEvent extends FlagEvent {
        private final FlagSpawn spawn;
        private final Location location;

        public FlagInitEvent(ArcadePlugin plugin, Flag flag, FlagSpawn spawn, Location location) {
            super(plugin, flag);

            this.spawn = spawn;
            this.location = location;
        }

        public FlagSpawn getSpawn() {
            return this.spawn;
        }

        public Location getLocation() {
            return this.location;
        }
    }
}
