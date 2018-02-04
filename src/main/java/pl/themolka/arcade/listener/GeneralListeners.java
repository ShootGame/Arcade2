package pl.themolka.arcade.listener;

import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.WorldInitEvent;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.BukkitCommands;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.respawn.PlayerRespawnEvent;
import pl.themolka.arcade.session.PlayerMoveEvent;

/**
 * <strong>General plugin listeners.</strong>
 *
 * Arrows Stuck:    There is a bug in Minecraft which doesn't remove arrows from
 *                  the player when he respawns. We need to fix it manually.
 * Ender Chests:    We need to disable ender chests on the whole server due to
 *                  the plugin logic incompatibility. Our multi-world system
 *                  doesn't work with the global ender chests. In the future, we
 *                  could add a fix which could override ender chests to be
 *                  per-world compatible.
 * Portals:         Other dimensions like Nether or The End are not supported.
 *                  Players should only play on the world loaded from their XML
 *                  files.
 * Commands:        Some commands are not supported and should never be used.
 * Weather:         We are not supporting weather changed dynamically by the
 *                  server itself. Modules are responsible for what and when
 *                  should be handled in the game world.
 * Custom Events:   We have some custom events. This is the best place to handle
 *                  them.
 */
public class GeneralListeners implements Listener {
    public static final String COMMAND_MESSAGE = ChatColor.RED +
            "You may not execute %s command on this server.";
    public static final String[] DISABLED_COMMANDS = {"reload", "stop"};
    public static final String ENDER_CHEST_MESSAGE = ChatColor.RED +
            "You may not %s Ender Chests on this server.";
    public static final String PORTAL_MESSAGE = ChatColor.RED +
            "You may not %s dimension portals on this server.";

    private final ArcadePlugin plugin;

    public GeneralListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // Fix Arrows Stuck
    //

    @Handler(priority = Priority.HIGHEST)
    public void fixArrowsStuck(PlayerRespawnEvent event) {
        event.getBukkitPlayer().setArrowsStuck(0);
    }

    //
    // Disable Ender Chests
    //

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestCraft(CraftItemEvent event) {
        if (event.getInventory().getResult() != null && event.getInventory()
                .getResult().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getActor().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "craft"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getActor().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "open"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "place"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderChestUse(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(String.format(
                    ENDER_CHEST_MESSAGE, "use"));
        }
    }

    //
    // Disable portals to other worlds/dimensions.
    //

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPortalCreate(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPortalUse(EntityPortalEvent event) {
        event.setCancelled(true);

        Entity creator = event.getEntity();
        if (creator instanceof Player) {
            creator.sendMessage(String.format(PORTAL_MESSAGE, "use"));
        }
    }

    //
    // Disable some commands
    //

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String prefix = BukkitCommands.BUKKIT_COMMAND_PREFIX.toLowerCase();
        String message = event.getMessage();
        if (!message.startsWith(prefix)) {
            return;
        }

        String query = message.substring(1);
        if (query.isEmpty()) {
            return;
        }

        String label = query.split(" ", 2)[0].toLowerCase();
        if (label.isEmpty()) {
            return;
        }

        Command command = this.plugin.getServer()
                .getCommandMap().getCommand(label);
        if (command == null) {
            return;
        }

        for (String disabled : DISABLED_COMMANDS) {
            for (String alias : command.getAliases()) {
                if (command.getName().equalsIgnoreCase(disabled) ||
                        alias.equalsIgnoreCase(disabled)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(String.format(
                            COMMAND_MESSAGE, prefix + command.getName()));
                    return;
                }
            }
        }
    }

    //
    // Disable weather and thunder cycle
    //

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
        event.setCancelled(true);
    }

    //
    // Custom Events
    //

    /**
     * Bukkit's {@link PlayerMoveEvent} is not what we need. We can simply
     * cancel the last player movement using the setCanceled(...) method in
     * {@link PlayerMoveEvent}.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game == null) {
            return;
        }

        GamePlayer player = game.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        PlayerMoveEvent wrapper = new PlayerMoveEvent(this.plugin,
                                                      player,
                                                      event);
        this.plugin.getEventBus().publish(wrapper);

        if (wrapper.isCanceled()) {
            Location to = event.getFrom().clone();
            to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);

            event.setTo(to);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setSpawnFlags(false, false);
    }
}
