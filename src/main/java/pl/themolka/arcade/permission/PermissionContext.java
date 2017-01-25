package pl.themolka.arcade.permission;

import org.bukkit.permissions.PermissionAttachment;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.ArrayList;
import java.util.List;

public class PermissionContext {
    private final ArcadePlugin plugin;

    private final PermissionAttachment attachment;
    private final List<Group> groups = new ArrayList<>();
    private final ArcadePlayer player;

    public PermissionContext(ArcadePlugin plugin, ArcadePlayer player) {
        this.plugin = plugin;

        this.attachment = player.getBukkit().addAttachment(plugin);
        this.player = player;
    }

    public boolean addGroup(Group group) {
        return this.groups.add(group);
    }

    public PermissionAttachment getAttachment() {
        return this.attachment;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public ArcadePlayer getPlayer() {
        return this.player;
    }

    public boolean hasPermission(String permission) {
        return this.getAttachment().getPermissions().getOrDefault(permission.toLowerCase(), false);
    }

    public boolean isMember(Group group) {
        return this.groups.contains(group);
    }

    public boolean isOperator() {
        for (Group group : this.getGroups()) {
            if (group.isOperator()) {
                return true;
            }
        }

        return false;
    }

    public void refresh() {
        this.refreshPermissions();

        boolean operator = this.isOperator();
        this.getPlayer().getBukkit().setOp(operator);

        if (!operator) {
            this.getPlayer().getBukkit().recalculatePermissions();
        }
    }

    public void refreshPermissions() {
        for (Group group : this.getGroups()) {
            for (Permission permission : group.getPermissions()) {
                this.getAttachment().setPermission(permission.getName(), permission.isAccess());
            }
        }
    }

    public boolean removeGroup(Group group) {
        return this.groups.remove(group);
    }
}
