package pl.themolka.arcade.capture.flag;

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.flag.state.CarryingState;
import pl.themolka.arcade.capture.flag.state.DroppedState;
import pl.themolka.arcade.capture.flag.state.FlagState;
import pl.themolka.arcade.capture.flag.state.SpawnedState;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameStartEvent;

import java.util.HashMap;
import java.util.Map;

public class FlagPhysicalRender implements Listener {
    public static final Material BANNER = Material.STANDING_BANNER;

    private final CaptureGame game;

    private final Map<Block, MaterialData> realBlocks = new HashMap<>();

    public FlagPhysicalRender(CaptureGame game) {
        this.game = game;
    }

    public void put(Block block, Flag flag) {
        this.realBlocks.put(block, new MaterialData(block.getType(), block.getData()));

        block.setType(BANNER);
        BannerUtils.toBlock((Banner) block.getState(), flag.getItem().getItemMeta());
    }

    public void put(FlagState.PhysicalFlag state, Flag flag) {
        Location location = state.getLocation();
        if (location != null) {
            this.put(location.getBlock(), flag);
        }
    }

    public void take(Block block) {
        MaterialData realBlock = this.realBlocks.get(block);
        block.setTypeIdAndData(realBlock.getItemTypeId(), realBlock.getData(), false);

        this.realBlocks.remove(block);
    }

    public void take(FlagState.PhysicalFlag state) {
        Location location = state.getLocation();
        if (location != null) {
            this.take(location.getBlock());
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagDropped(FlagDroppedEvent event) {
        FlagState state = event.getNewState();
        if (!event.isCanceled() && state instanceof DroppedState) {
            this.put((DroppedState) state, event.getFlag());
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagPickedUp(FlagPickedUpEvent event) {
        FlagState state = event.getNewState();
        if (!event.isCanceled() && state instanceof CarryingState) {
            this.take(((CarryingState) state).getSource().getBlock());
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagRespawned(FlagRespawnedEvent event) {
        FlagState state = event.getNewState();
        if (!event.isCanceled() && state instanceof SpawnedState) {
            this.put((SpawnedState) state, event.getFlag());
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagRespawning(FlagRespawningEvent event) {
        FlagState state = event.getOldState();
        if (!event.isCanceled() && state instanceof DroppedState) {
            this.take((DroppedState) state);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onGameStart(GameStartEvent event) {
        for (Capturable capturable : this.game.getCapturables()) {
            if (capturable instanceof Flag) {
                for (FlagSpawn spawn : ((Flag) capturable).getSpawns()) {
                    Banner banner = spawn.getBanner();

                    if (banner != null) {
                        banner.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }
}
