package pl.themolka.arcade.permission;

import pl.themolka.arcade.ArcadePlugin;

public class PermissionsReloadedEvent extends PermissionsEvent {
    public PermissionsReloadedEvent(ArcadePlugin plugin, PermissionManager permissions) {
        super(plugin, permissions);
    }
}
