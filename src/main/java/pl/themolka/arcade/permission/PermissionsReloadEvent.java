package pl.themolka.arcade.permission;

import pl.themolka.arcade.ArcadePlugin;

public class PermissionsReloadEvent extends PermissionsEvent {
    public PermissionsReloadEvent(ArcadePlugin plugin, PermissionManager permissions) {
        super(plugin, permissions);
    }
}
