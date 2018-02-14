package pl.themolka.arcade.capture.point;

import net.engio.mbassy.listener.Handler;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.CapturedState;
import pl.themolka.arcade.capture.point.state.NeutralState;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameStartEvent;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.team.TeamEditEvent;
import pl.themolka.arcade.util.Color;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PointRegionRender implements Listener {
    public static final Set<Material> RENDERABLE_MATERIALS = Stream.of(
            Material.CARPET,
            Material.STAINED_CLAY,
            Material.STAINED_GLASS,
            Material.STAINED_GLASS_PANE,
            Material.WOOL).collect(Collectors.toSet());

    private final CaptureGame game;

    public PointRegionRender(CaptureGame game) {
        this.game = game;
    }

    public boolean isRenderable(Block block) {
        return block != null && this.isRenderable(block.getType());
    }

    public boolean isRenderable(Material material) {
        return material != null && RENDERABLE_MATERIALS.contains(material);
    }

    public boolean isRenderable(MaterialData data) {
        return data != null && this.isRenderable(data.getItemType());
    }

    public void renderAllPoints() {
        for (Capturable capturable : this.game.getCapturables()) {
            if (capturable instanceof Point) {
                this.renderPoint((Point) capturable);
            }
        }
    }

    public boolean renderBanner(Banner banner, DyeColor color) {
        banner.setBaseColor(color);
        return banner.update(false, false);
    }

    public boolean renderBanner(BlockState blockState, DyeColor color) {
        return blockState instanceof Banner && this.renderBanner((Banner) blockState, color);
    }

    public boolean renderBlock(Block block, DyeColor color) {
        Material material = block.getType();

        if (this.isRenderable(material)) {
            block.setData(color.getWoolData());
            return true;
        } else if (material.equals(Material.STANDING_BANNER)
                || material.equals(Material.WALL_BANNER)) {
            this.renderBanner(block.getState(), color);
            return true;
        }

        return false;
    }

    public void renderBlocks(Iterable<Block> blocks, DyeColor color) {
        for (Block block : blocks) {
            this.renderBlock(block, color);
        }
    }

    public void renderPoint(Point point) {
        Participator owner = point.getOwner();
        this.renderPoint(point, owner != null ? owner.getColor() : point.getNeutralColor());
    }

    public void renderPoint(Point point, Color color) {
        this.renderPoint(point, color.toDye());
    }

    public void renderPoint(Point point, DyeColor color) {
        this.renderRegion(point.getStateRegion(), color);
    }

    public void renderRegion(Region region, DyeColor color) {
        this.renderBlocks(region.getBlocks(), color);
    }

    @Handler(priority = Priority.LAST)
    public void initialRender(GameStartEvent event) {
        this.renderAllPoints();
    }

    @Handler(priority = Priority.LAST)
    public void renderParticipatorEdit(TeamEditEvent event) {
        if (event.getReason().equals(TeamEditEvent.Reason.PAINT)) {
            this.renderAllPoints();
        }
    }

    @Handler(priority = Priority.LAST)
    public void renderCaptured(PointCapturedEvent event) {
        if (!event.isCanceled() && event.getNewState() instanceof CapturedState) {
            this.renderPoint(event.getPoint(), event.getNewOwner().getColor());
        }
    }

    @Handler(priority = Priority.LAST)
    public void renderNeutral(PointLostEvent event) {
        if (!event.isCanceled() && event.getNewState() instanceof NeutralState) {
            Point point = event.getPoint();
            this.renderPoint(point, point.getNeutralColor());
        }
    }
}
