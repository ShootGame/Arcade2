package pl.themolka.arcade.permission;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.PluginReadyEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.ServerCycleEvent;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.PlayerJoinEvent;
import pl.themolka.arcade.team.PlayerJoinedTeamEvent;

public class PermissionListeners {
    private final ArcadePlugin plugin;

    public PermissionListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    // player joins the server
    @Handler(priority = Priority.LOWER)
    public void onPlayerJoinServer(PlayerJoinEvent event) {
        this.refresh(event.getPlayer());
    }

    // server cycles the game
    @Handler(priority = Priority.LOWER)
    public void onServerCycle(ServerCycleEvent event) {
        for (GamePlayer player : event.getNewGame().getPlayers()) {
            this.refresh(player.getPlayer());
        }
    }

    // server starts
    @Handler(priority = Priority.LOWER)
    public void onServerStart(PluginReadyEvent event) {
        for (ArcadePlayer player : event.getPlugin().getPlayers()) {
            this.refresh(player);
        }
    }

    public void refresh(ArcadePlayer player) {
        if (player != null) {
            player.getPermissions().clearGroups(); // we want to have fresh groups from the storage
            player.getPermissions().refresh();
        }
    }

    //
    // Teams Module
    //

    @Handler(priority = Priority.LOWER)
    public void onPlayerSwitchedTeam(PlayerJoinedTeamEvent event) {
        this.refresh(event.getPlayer());
    }
}
