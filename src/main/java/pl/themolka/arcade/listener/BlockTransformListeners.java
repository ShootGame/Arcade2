package pl.themolka.arcade.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFallEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.Collections;
import java.util.List;

/**
 * Listeners related to {@link BlockTransformEvent}.
 */
public class BlockTransformListeners implements Listener {
    private final ArcadePlugin plugin;

    public BlockTransformListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.postWrapped(event, event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.postWrapped(event, null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (this.postWrapped(event, null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFall(BlockFallEvent event) {
        if (this.postWrapped(event, null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        if (this.postWrapped(event, event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (this.postWrapped(event, null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (this.postWrapped(event, null, event.getBlocks())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (this.postWrapped(event, null, event.getBlocks())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.postWrapped(event, event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private boolean postWrapped(BlockEvent source, Player player, Block block) {
        return this.postWrapped(source, player, Collections.singletonList(block));
    }

    private boolean postWrapped(BlockEvent source, Player player, List<Block> blocks) {
        ArcadePlayer arcade = null;
        if (player != null) {
            arcade = this.plugin.getPlayer(player);
        }

        BlockTransformEvent event = new BlockTransformEvent(this.plugin, blocks, arcade, source);
        this.plugin.getEventBus().publish(event);

        return event.isCanceled();
    }
}
