package pl.themolka.arcade.capture.point;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.NeutralState;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameStartEvent;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.util.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PointRegionRender implements Listener {
    public static final PatternType BANNER_PATTERN_TYPE = PatternType.BASE;
    public static final Set<Material> DEFAULT_POINT_MATERIALS = Stream.of(
            Material.CARPET,
            Material.STAINED_CLAY,
            Material.STAINED_GLASS,
            Material.STAINED_GLASS_PANE,
            Material.WOOL).collect(Collectors.toSet());

    private final CaptureGame game;

    private final Set<Material> pointMaterials;

    public PointRegionRender(CaptureGame game) {
        this(game, DEFAULT_POINT_MATERIALS);
    }

    public PointRegionRender(CaptureGame game, Set<Material> pointMaterials) {
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

    public void renderBanner(Banner banner, DyeColor color) {
        banner.setBaseColor(color);
        banner.setPatterns(Collections.singletonList(new Pattern(color, BANNER_PATTERN_TYPE)));
    }

    public void renderBanner(BlockState blockState, DyeColor color) {
        if (blockState instanceof Banner) {
            this.renderBanner((Banner) blockState, color);
        }
    }

    public void renderBlocks(Point point, ChatColor color) {
        this.renderBlocks(point, Color.ofChat(color));
    }

    public void renderBlocks(Point point, Color color) {
        this.renderBlocks(point, color.toDye());
    }

    public void renderBlocks(Point point, DyeColor color) {
        for (Block block : point.getStateRegion().getBlocks()) {
            Material type = block.getType();

            if (this.isRenderable(block)) {
                block.setData(color.getWoolData());
            } else if (type.equals(Material.STANDING_BANNER)
                    || type.equals(Material.WALL_BANNER)) {
                this.renderBanner(block.getState(), color);
            }
        }
    }

    @Handler(priority = Priority.LAST)
    public void initialRender(GameStartEvent event) {
        for (Capturable capturable : this.game.getCapturables()) {
            if (capturable instanceof Point) {
                Point point = (Point) capturable;
                GoalHolder owner = point.getOwner();

                Color color = Color.ofChat(NeutralState.NEUTRAL_COLOR);
                if (owner != null) {
                    color = owner.getColor();
                }

                this.renderBlocks(point, color);
            }
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
