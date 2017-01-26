package pl.themolka.arcade.permission;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

public class PermissionsEvent extends Event {
    private final PermissionManager permissions;

    public PermissionsEvent(ArcadePlugin plugin, PermissionManager permissions) {
        super(plugin);

        this.permissions = permissions;
    }

    public PermissionManager getPermissions() {
        return this.permissions;
    }
}
