package pl.themolka.arcade.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFallEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;
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
        if (this.postWrapped(event, new MaterialData(Material.AIR), event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.postWrapped(event, new MaterialData(Material.FIRE), null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (this.postWrapped(event, new MaterialData(Material.AIR), null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFall(BlockFallEvent event) {
        if (this.postWrapped(event, new MaterialData(Material.AIR), null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (this.postWrapped(event, event.getNewState().getMaterialData(), null, event.getBlock())) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onBlockFromTo(BlockFromToEvent event) {
//        if (this.postWrapped(event, event.getToBlock().getState().getMaterialData(), null, event.getBlock())) {
//            event.setCancelled(true);
//        }
//    }

    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        if (this.postWrapped(event, event.getBlockReplacedState().getMaterialData(), event.getPlayer(), event.getBlockAgainst())) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onBlockPhysics(BlockPhysicsEvent event) {
//        if (this.postWrapped(event, new MaterialData(event.getChangedType()), null, event.getBlock())) {
//            event.setCancelled(true);
//        }
//    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (this.postWrapped(event, new MaterialData(Material.AIR), null, event.getBlocks())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (this.postWrapped(event, new MaterialData(Material.AIR), null, event.getBlocks())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.postWrapped(event, new MaterialData(Material.AIR), event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private boolean postWrapped(BlockEvent source, MaterialData newState, Player player, Block block) {
        return this.postWrapped(source, newState, player, Collections.singletonList(block));
    }

    private boolean postWrapped(BlockEvent source, MaterialData newState, Player player, List<Block> blocks) {
        if (source.isCancelled()) {
            return false;
        }

        ArcadePlayer arcade = null;
        if (player != null) {
            arcade = this.plugin.getPlayer(player);
        }

        for (Block block : blocks) {
            BlockTransformEvent event = new BlockTransformEvent(this.plugin, newState, arcade, new WrappedBlockEvent(block, source));
            this.plugin.getEventBus().publish(event);

            if (event.isCanceled()) {
                return true;
            }
        }

        return false;
    }

    private class WrappedBlockEvent extends BlockEvent {
        private final BlockEvent event;

        public WrappedBlockEvent(Block block, BlockEvent event) {
            super(block);

            this.event = event;
        }

        public BlockEvent getEvent() {
            return this.event;
        }
    }
}
