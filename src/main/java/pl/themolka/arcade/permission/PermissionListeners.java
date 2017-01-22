package pl.themolka.arcade.permission;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadePlayerJoinEvent;
import pl.themolka.arcade.team.PlayerJoinedTeamEvent;

public class PermissionListeners {
    private final ArcadePlugin plugin;

    public PermissionListeners(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Handler(priority = Priority.LOWER)
    public void onPlayerJoinServer(ArcadePlayerJoinEvent event) {
        this.refresh(event.getPlayer());
    }

    @Handler(priority = Priority.LOWER)
    public void onPlayerTeamSwitched(PlayerJoinedTeamEvent event) {
        this.refresh(event.getPlayer());
    }

    private void refresh(ArcadePlayer player) {
        if (player != null) {
            player.getPermissions().refresh();
        }
    }
}
