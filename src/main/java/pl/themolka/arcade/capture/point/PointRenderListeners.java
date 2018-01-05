package pl.themolka.arcade.capture.point;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.NeutralState;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalCreateEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PointRenderListeners implements Listener {
    public static final Set<Material> DEFAULT_POINT_MATERIALS = Stream.of(
            Material.CARPET,
            Material.STAINED_CLAY,
            Material.STAINED_GLASS,
            Material.STAINED_GLASS_PANE,
            Material.WOOL).collect(Collectors.toSet());

    private final CaptureGame game;

    private final Set<Material> pointMaterials;

    public PointRenderListeners(CaptureGame game) {
        this(game, DEFAULT_POINT_MATERIALS);
    }

    public PointRenderListeners(CaptureGame game, Set<Material> pointMaterials) {
        this.game = game;

        this.pointMaterials = pointMaterials;
    }

    public List<Material> getPointMaterials() {
        return new ArrayList<>(this.pointMaterials);
    }

    public boolean isRenderable(Block block) {
        return block != null && this.isRenderable(block.getType());
    }

    public boolean isRenderable(Material material) {
        return material != null && this.pointMaterials.contains(material);
    }

    public boolean isRenderable(MaterialData data) {
        return data != null && this.isRenderable(data.getItemType());
    }

    public void renderBlocks(Point point, ChatColor color) {
        this.renderBlocks(point, Color.ofChat(color));
    }

    public void renderBlocks(Point point, Color color) {
        this.renderBlocks(point, color.toDye());
    }

    public void renderBlocks(Point point, DyeColor color) {
        Region region = point.getStateRegion();
        for (Block block : region.getBlocks()) {
            if (this.isRenderable(block)) {
                block.setData(color.getWoolData());
            }
        }
    }

    @Handler(priority = Priority.LAST)
    public void initialRender(GoalCreateEvent event) {
        Goal goal = event.getGoal();
        if (goal instanceof Point) {
            Point point = (Point) goal;
            GoalHolder owner = point.getOwner();

            ChatColor color = NeutralState.NEUTRAL_COLOR;
            if (owner != null) {
                Color ownerColor = owner.getColor();

                if (ownerColor != null) {
                    color = ownerColor.toChat();
                }
            }

            this.renderBlocks(point, color);
        }
    }

    @Handler(priority = Priority.LAST)
    public void renderCaptured(PointCapturedEvent event) {
        if (!event.isCanceled() && event.getNewState() instanceof CapturedState) {
            this.renderBlocks(event.getPoint(), event.getNewOwner().getColor());
        }
    }

    @Handler(priority = Priority.LAST)
    public void renderNeutral(PointLostEvent event) {
        if (!event.isCanceled() && event.getNewState() instanceof NeutralState) {
            this.renderBlocks(event.getPoint(), NeutralState.NEUTRAL_COLOR);
        }
    }
}
