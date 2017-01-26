package pl.themolka.arcade.leak;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.InteractableGoal;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.region.CuboidRegion;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionBounds;
import pl.themolka.arcade.util.StringId;

import java.util.List;

public class Leakable implements InteractableGoal, StringId {
    public static final int DEFAULT_DETECTOR_LEVEL = 5;
    public static final String DEFAULT_GOAL_NAME = "Leakable";
    public static final String DETECTOR_REGION_SUFFIX = "_detector-region";

    private final LeakGame game;

    private final MatchWinner owner;
    private boolean completed;
    private Region detector;
    private final String id;
    private final Liquid liquid;
    private String name;
    private Region region;
    private boolean touched;

    public Leakable(LeakGame game, MatchWinner owner, String id) {
        this.game = game;

        this.owner = owner;
        this.id = id;
        this.liquid = new Liquid();
    }

    @Override
    public String getGoalInteractMessage(String interact) {
        return interact + ChatColor.LIGHT_PURPLE + " broke a piece of " + ChatColor.GREEN + ChatColor.BOLD +
                ChatColor.ITALIC + this.getName() + ChatColor.RESET + ChatColor.LIGHT_PURPLE + ".";
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        if (this.hasName()) {
            return this.name;
        }

        return DEFAULT_GOAL_NAME;
    }

    @Override
    public boolean isCompletableBy(MatchWinner winner) {
        return !this.getOwner().equals(winner);
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public boolean isUntouched() {
        return !this.touched;
    }

    @Override
    public boolean reset() {
        if (!this.isCompleted()) {
            return false;
        }

        LeakableResetEvent event = new LeakableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.game.getPlugin().getEventBus().publish(new GoalResetEvent(this.game.getPlugin(), this));

            this.completed = false;
            this.touched = false;
            return true;
        }

        return false;
    }

    @Override
    public void setCompleted(MatchWinner winner, boolean completed) {
        if (completed) {
            this.handleGoalComplete();
        } else {
            this.reset();
        }
    }

    /**
     * Events called in this method:
     *   - LeakableBreakEvent (cancelable)
     *   - GoalProgressEvent
     */
    public boolean breakPiece(MatchWinner breaker, GamePlayer player) {
        String interactMessage = breaker.getTitle();
        if (player != null) {
            interactMessage = player.getFullName();
        }

        LeakableBreakEvent event = new LeakableBreakEvent(this.game.getPlugin(), this, breaker);
        this.game.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return false;
        }

        double oldProgress = this.getProgress();

        this.touched = true;
        breaker.sendGoalMessage(this.getGoalInteractMessage(interactMessage));

        this.game.getPlugin().getEventBus().publish(new GoalProgressEvent(this.game.getPlugin(), this, oldProgress));
        return true;
    }

    public void build(Liquid.Type type, Region region, int detectorLevel) {
        // region
        List<Block> blocks = region.getBlocks(); // may take a while
        this.setRegion(region);

        // detector
        if (detectorLevel == 0) {
            detectorLevel = DEFAULT_DETECTOR_LEVEL;
        }
        int distance = detectorLevel * 3;

        RegionBounds bounds = region.getBounds();
        Vector min = bounds.getMin().clone().subtract(distance, 0, distance).setY(Region.MIN_HEIGHT);
        Vector max = bounds.getMax().clone().add(distance, 0, distance).setY(bounds.getMin().getY() - detectorLevel);
        this.setDetector(new CuboidRegion(region.getId() + DETECTOR_REGION_SUFFIX, region.getMap(), min, max));

        // liquid
        Liquid liquid = this.getLiquid();
        if (type == null) {
            type = liquid.findType(blocks); // may take a while
        }

        liquid.setType(type);
        liquid.addSnapshot(liquid.createSnapshot(blocks)); // may take a while
    }

    public Region getDetector() {
        return this.detector;
    }

    public Liquid getLiquid() {
        return this.liquid;
    }

    public MatchWinner getOwner() {
        return this.owner;
    }

    public Region getRegion() {
        return this.region;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public void leak() {
        this.setCompleted(null, true);
    }

    public void setDetector(Region detector) {
        this.detector = detector;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    private void handleGoalComplete() {
        if (this.completed) {
            return;
        }
        this.completed = true;

        LeakableLeakEvent event = new LeakableLeakEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            // This game for this `MatchWinner` has been completed - we can tell
            // it to the plugin, so it can end the match. This method will loop
            // all `MatchWinner`s (like players or teams) to find the winner.
            this.game.getPlugin().getEventBus().publish(new GoalCompleteEvent(this.game.getPlugin(), this));
        }
    }
}
