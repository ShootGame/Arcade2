package pl.themolka.arcade.region;

import net.engio.mbassy.listener.Handler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.OptionalResult;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class RegionListeners implements Listener {
    private final RegionsGame regions;

    public RegionListeners(RegionsGame regions) {
        this.regions = regions;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Region region = this.regions.fetch(event.getBlock());
        AbstainableResult result = this.getFilterResult(region, RegionEventType.BLOCK_BREAK, event.getPlayer(), null, event.getBlock());

        if (result.isFalse()) {
            event.setCancelled(true);
            return;
        }

        AbstainableResult buildResult = this.getFilterResult(region, RegionEventType.BLOCK, event.getPlayer(), null, event.getBlock());
        if (buildResult.isFalse()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Region region = this.regions.fetch(event.getBlock());
        AbstainableResult result = this.getFilterResult(region, RegionEventType.BLOCK_PHYSICS, null, null, event.getBlock());

        if (result.isFalse()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Region region = this.regions.fetch(event.getBlock());

        Block against = event.getBlockAgainst();
        if (against != null) {
            AbstainableResult result = this.getFilterResult(region, RegionEventType.BLOCK_PLACE_AGAINST, event.getPlayer(), null, against);
            if (result.isFalse()) {
                event.setCancelled(true);
                return;
            }
        }

        AbstainableResult result = this.getFilterResult(region, RegionEventType.BLOCK_PLACE, event.getPlayer(), null, event.getBlock());
        if (result.isFalse()) {
            event.setCancelled(true);
            return;
        }

        AbstainableResult buildResult = this.getFilterResult(region, RegionEventType.BLOCK, event.getPlayer(), null, event.getBlock());
        if (buildResult.isFalse()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Region region = this.regions.fetch(block);
        AbstainableResult result = this.getFilterResult(region, RegionEventType.USE, event.getPlayer(), null, block);

        if (result.isFalse()) {
            event.setCancelled(true);
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Region from = this.regions.fetch(event.getFrom());
        Region to = this.regions.fetch(event.getTo());

        if (from == null || to == null || from.equals(to)) {
            return;
        }

        AbstainableResult fromResult = this.getFilterResult(from, RegionEventType.LEAVE, event.getPlayer().getBukkit(),
                event.getGamePlayer(), event.getTo().getBlock());
        if (fromResult.isFalse()) {
            event.setCanceled(true);
            return;
        }

        AbstainableResult toResult = this.getFilterResult(to, RegionEventType.ENTER, event.getPlayer().getBukkit(),
                event.getGamePlayer(), event.getTo().getBlock());
        if (toResult.isFalse()) {
            event.setCanceled(true);
        }
    }

    private AbstainableResult getFilterResult(Region region, RegionEventType type, Player player, GamePlayer game, Block block) {
        if (region == null) {
            return OptionalResult.ABSTAIN;
        }

        Filter filter = null;
        if (filter == null) {
            return OptionalResult.ABSTAIN;
        }

        if (game == null && player != null) {
            game = this.regions.getGame().getPlayer(player);
        }

        List<Object> data = new ArrayList<>();
        if (player != null) {
            data.add(player);
        }
        if (game != null) {
            data.add(game);
        }
        data.add(block);

        return filter.filter(data.toArray(new Object[data.size()]));
    }
}
