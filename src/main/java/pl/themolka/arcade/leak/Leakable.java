package pl.themolka.arcade.leak;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalContributionContext;
import pl.themolka.arcade.goal.GoalContributor;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.InteractableGoal;
import pl.themolka.arcade.match.MatchWinner;
import pl.themolka.arcade.region.CuboidRegion;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionBounds;
import pl.themolka.arcade.util.StringId;

import java.util.ArrayList;
import java.util.List;

public class Leakable implements InteractableGoal, StringId {
    public static final int DEFAULT_DETECTOR_LEVEL = 5;
    public static final String DEFAULT_GOAL_NAME = "Leakable";
    public static final Material DEFAULT_MATERIAL = Material.OBSIDIAN;
    public static final String DETECTOR_REGION_SUFFIX = "_detector-region";

    private final LeakGame game;

    private final MatchWinner owner;
    private final List<Vector> breaked;
    private boolean completed;
    private final GoalContributionContext contributions;
    private Region detector;
    private final String id;
    private final Liquid liquid;
    private List<Material> material;
    private String name;
    private Region region;

    public Leakable(LeakGame game, MatchWinner owner, String id) {
        this.game = game;

        this.owner = owner;
        this.breaked = new ArrayList<>();
        this.contributions = new GoalContributionContext();
        this.id = id;
        this.liquid = new Liquid();
    }

    @Override
    public GoalContributionContext getContributions() {
        return this.contributions;
    }

    @Override
    public String getGoalInteractMessage(String interact) {
        return ChatColor.GOLD + interact + ChatColor.DARK_PURPLE + " broke a piece of " +
                ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.DARK_PURPLE +
                "'s " + ChatColor.GOLD + ChatColor.BOLD + ChatColor.ITALIC +
                this.getName() + ChatColor.RESET + ChatColor.DARK_PURPLE + ".";
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
    public boolean reset() {
        if (!this.isCompleted()) {
            return false;
        }

        LeakableResetEvent event = new LeakableResetEvent(this.game.getPlugin(), this);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            this.game.getPlugin().getEventBus().publish(new GoalResetEvent(this.game.getPlugin(), this));

            this.completed = false;
            this.breaked.clear();
            this.contributions.clearContributors();
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
    public boolean breakPiece(MatchWinner breaker, GamePlayer player, Vector vector) {
        String interactMessage = breaker.getTitle();
        if (player != null) {
            interactMessage = player.getFullName();
        }

        LeakableBreakEvent event = new LeakableBreakEvent(this.game.getPlugin(), this, breaker, vector);
        this.game.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return false;
        }

        double oldProgress = this.getProgress();

        this.breaked.add(vector);
        if (player != null) {
            this.getContributions().addContributor(player);
        }

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

    public List<Material> getMaterial() {
        return this.material;
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

    public boolean contains(Block block) {
        Vector vector = new Vector(block.getX(), block.getY(), block.getZ());
        return this.matchMaterial(block.getType()) &&
                this.getRegion().contains(block) &&
                !this.breaked.contains(vector);
    }

    public void leak() {
        String message = ChatColor.GOLD + this.getOwner().getTitle() + ChatColor.DARK_PURPLE +
                "'s " + ChatColor.GOLD + this.getName() + ChatColor.DARK_PURPLE + " has leaked";

        List<GoalContributor> contributions = this.getContributions().getContributors();
        if (!contributions.isEmpty()) {
            StringBuilder builder = new StringBuilder(" by ");
            int max = 5;
            int size = contributions.size();

            int touches = 0;
            for (GoalContributor contributor : contributions) {
                touches += contributor.getTouches();
            }

            for (int i = 0; i < size; i++) {
                builder.append(ChatColor.RESET);

                GoalContributor contributor = contributions.get(i);
                String name = contributor.getDisplayName();
                int percentage = Math.round((100F / touches) * contributor.getTouches());

                if (i != 0) {
                    builder.append(ChatColor.DARK_PURPLE);
                    if (i + 1 == size) {
                        builder.append(" and ");
                    } else if (i + 1 == max) {
                        builder.append(" and ").append(ChatColor.GOLD).append(size - max)
                                .append(ChatColor.DARK_PURPLE).append(" more..");
                        break;
                    } else {
                        builder.append(", ");
                    }
                }

                builder.append(ChatColor.GOLD).append(name).append(ChatColor.RESET)
                        .append(ChatColor.DARK_PURPLE).append(" (").append(ChatColor.GREEN)
                        .append(percentage).append("%").append(ChatColor.DARK_PURPLE).append(")");
            }

            message += builder.toString();
        }

        this.game.getMatch().sendGoalMessage(message + ChatColor.DARK_PURPLE + ".");
        this.setCompleted(null, true);
    }

    public boolean matchMaterial(Material material) {
        for (Material type : this.getMaterial()) {
            if (type.equals(material)) {
                return true;
            }
        }

        return false;
    }

    public void setDetector(Region detector) {
        this.detector = detector;
    }

    public void setMaterial(List<Material> material) {
        this.material = material;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(Region region) {
        this.region = region;
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
