package pl.themolka.arcade.capture.flag;

import net.engio.mbassy.listener.Handler;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.themolka.arcade.capture.Capturable;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.PlayerQuitEvent;
import pl.themolka.arcade.team.PlayerLeaveTeamEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlagPayloadRender implements Listener {
    private final CaptureGame game;

    private final Map<GamePlayer, ItemStack> realHelmets = new HashMap<>();

    public FlagPayloadRender(CaptureGame game) {
        this.game = game;
    }

    public void attach(GamePlayer player, FlagItem item) {
        PlayerInventory inventory = player.getBukkit().getInventory();
        this.realHelmets.put(player, inventory.getHelmet().clone());

        inventory.setHelmet(item.clone());
    }

    public void detach(GamePlayer player, FlagItem item, boolean attachHelmet) {
        this.removeFlagItems(item, player);

        if (attachHelmet && this.realHelmets.containsKey(player)) {
            ItemStack realHelmet = this.realHelmets.get(player);
            player.getBukkit().getInventory().setHelmet(realHelmet);
        }

        this.realHelmets.remove(player);
    }

    public void detachAll(GamePlayer player, boolean attachHelmet) {
        for (Capturable capturable : this.game.getCapturables()) {
            if (capturable instanceof Flag) {
                this.detach(player, ((Flag) capturable).getItem(), attachHelmet);
            }
        }
    }

    public void removeFlagItems(FlagItem item, GamePlayer player) {
        PlayerInventory inventory = player.getBukkit().getInventory();
        for (ItemStack itemStack : new ArrayList<>(inventory.contents())) {
            if (item.isSimilar(itemStack)) {
                inventory.remove(itemStack);
            }
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagCaptured(FlagCapturedEvent event) {
        if (!event.isCanceled()) {
            this.detach(event.getCapturer(), event.getFlag().getItem(), true);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagDropped(FlagDroppedEvent event) {
        if (!event.isCanceled()) {
            this.detach(event.getDropper(), event.getFlag().getItem(), true);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onFlagPickedUp(FlagPickedUpEvent event) {
        if (!event.isCanceled()) {
            this.attach(event.getPicker(), event.getFlag().getItem());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        for (Capturable capturable : this.game.getCapturables()) {
            if (capturable instanceof Flag && ((Flag) capturable).getItem().isSimilar(item)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GamePlayer player = this.game.getGame().getPlayer(event.getEntity());
        if (player != null) {
            this.detachAll(player, false);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerLeaveTeam(PlayerLeaveTeamEvent event) {
        if (!event.isCanceled()) {
            this.detachAll(event.getGamePlayer(), false);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.realHelmets.remove(event.getGamePlayer());
    }
}
