/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.ArrayList;

/**
 * Listeners related to {@link BlockTransformEvent}.
 */
@ServiceId("BlockTransformEvent")
public class BlockTransformEventService extends Service {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        this.post(event,
                  event.getBlock().getState(),
                  this.applyAir(event.getBlock()),
                  event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        this.post(event,
                  event.getBlock().getState(),
                  this.applyAir(event.getBlock()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!this.isBucket(event.getItem().getType())) {
            return;
        }

        Block target = event.getVelocity().toLocation(event.getBlock().getWorld()).getBlock();
        Material content = this.getBucketContent(event.getItem().getType());

        if (!content.equals(Material.AIR)) {
            this.post(event,
                      target.getState(),
                      this.applyState(target, content));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        this.post(event,
                  event.getBlock().getState(),
                  event.getNewState());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFall(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock)) {
            return;
        }

        Block block = event.getLocation().getBlock();

        this.post(event,
                  block.getState(),
                  this.applyAir(block));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        this.post(event,
                  event.getBlock().getState(),
                  event.getNewState());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType().equals(event.getToBlock().getType())) {
            return;
        }

        this.post(event,
                  event.getToBlock().getState(),
                  event.getBlock().getState());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        this.post(event,
                  event.getBlock().getState(),
                  event.getNewState());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        BlockIgniteEvent.IgniteCause cause = BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL;
        if (event.getCause().equals(cause)) {
            return;
        }

        BlockState newState = this.applyState(event.getBlock(), Material.FIRE);
        this.post(event,
                  event.getBlock().getState(),
                  newState,
                  event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event instanceof BlockMultiPlaceEvent) {
            BlockMultiPlaceEvent multi = (BlockMultiPlaceEvent) event;
            for (BlockState state : multi.getReplacedBlockStates()) {
                this.post(event,
                          state,
                          state.getBlock().getState(),
                          event.getPlayer());
            }
        } else {
            this.post(event,
                      event.getBlockReplacedState(),
                      event.getBlock().getState(),
                      event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (!event.getNewState().getType().equals(Material.FIRE)) {
            this.post(event,
                      event.getBlock().getState(),
                      event.getNewState());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Block block = event.getBlock();

        boolean arrow = event.getEntity() instanceof Arrow;
        if (arrow && event.getTo().equals(Material.AIR) && block.getType().equals(Material.TNT)) {
            return;
        }

        this.post(event,
                  block.getState(),
                  this.applyState(block, event.getTo(), event.getBlockData()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : new ArrayList<>(event.blockList())) {
            if (block.getType().equals(Material.TNT)) {
                continue;
            }

            boolean cancel = this.post(event,
                                       block,
                                       block.getState(),
                                       this.applyAir(block),
                                       null,
                                       false);

            if (cancel) {
                event.blockList().remove(block);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            Block block = event.getEntity().getLocation().getBlock();
            if (!block.getType().equals(Material.TNT)) {
                return;
            }

            this.post(event,
                      block.getState(),
                      this.applyAir(block));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        // TODO
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetrack(BlockPistonRetractEvent event) {
        // TODO
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Material inside = this.getBucketContent(block.getType());

        if (inside != null) {
            this.post(event,
                      block.getState(),
                      this.applyState(block, inside),
                      event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        this.post(event,
                  block.getState(),
                  this.applyAir(block),
                  event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block != null && block.getType().equals(Material.FARMLAND)) {
            this.post(event,
                      block.getState(),
                      this.applyState(block, Material.DIRT),
                      event.getPlayer());
        }
    }

    //
    // Block States
    //

    private BlockState applyAir(Block from) {
        return this.applyState(from, Material.AIR);
    }

    private BlockState applyState(Block from, Material type) {
        return this.applyState(from, type, from.getBlockData());
    }

    private BlockState applyState(Block from, Material type, BlockData data) {
        BlockState state = from.getState();
        state.setType(type);
        state.setBlockData(data);
        return state;
    }

    public Material getBucketContent(Material bucket) {
        if (this.isBucket(bucket)) {
            switch (bucket) {
                case BUCKET:
                case MILK_BUCKET:
                    return Material.AIR;
                case LAVA_BUCKET:
                    return Material.LAVA;
                case WATER_BUCKET:
                    return Material.LAVA;
            }
        }

        return null;
    }

    private boolean isBucket(Material material) {
        switch (material) {
            case BUCKET:
            case LAVA:
            case MILK_BUCKET:
            case WATER_BUCKET:
                return true;
            default:
                return false;
        }
    }

    //
    // Posting
    //

    private boolean post(Event cause, BlockState oldState,
                         BlockState newState) {
        return this.post(cause, oldState.getBlock(), oldState,
                         newState);
    }

    private boolean post(Event cause, BlockState oldState,
                         BlockState newState, Player bukkit) {
        return this.post(cause, oldState.getBlock(), oldState,
                         newState, bukkit);
    }

    private boolean post(Event cause, Block block, BlockState oldState,
                         BlockState newState) {
        return this.post(cause, block, oldState,
                         newState, null);
    }

    public boolean post(Event cause, Block block, BlockState oldState,
                        BlockState newState, Player bukkit) {
        return this.post(cause, block, oldState,
                         newState, bukkit, true);
    }

    private boolean post(Event cause, Block block, BlockState oldState,
                         BlockState newState, Player bukkit, boolean cancel) {
        ArcadePlugin plugin = this.getPlugin();

        ArcadePlayer player = null;
        if (bukkit != null) {
            player = plugin.getPlayer(bukkit);
        }

        BlockTransformEvent event = new BlockTransformEvent(plugin, block, cause, newState, oldState, player);
        plugin.getEventBus().publish(event);

        boolean canceled = cancel && event.isCanceled() &&
                cause instanceof Cancellable;
        if (canceled) {
            ((Cancellable) cause).setCancelled(true);
        }

        return event.isCanceled();
    }
}
