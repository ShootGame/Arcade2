package pl.themolka.arcade.permission;

import org.bukkit.permissions.PermissionAttachment;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.ArrayList;
import java.util.List;

public class PermissionContext {
    public static final String EMPTY_PREFIX = "";

    private final ArcadePlugin plugin;

    private final PermissionAttachment attachment;
    private final List<Group> groups = new ArrayList<>();
    private final ArcadePlayer player;
    private String prefixes;

    public PermissionContext(ArcadePlugin plugin, ArcadePlayer player) {
        this.plugin = plugin;

        this.attachment = player.getBukkit().addAttachment(plugin);
        this.player = player;
    }

    public boolean addGroup(Group group) {
        return !this.isMember(group) && this.groups.add(group);
    }

    public void clearGroups() {
        this.groups.clear();
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

    public String getPrefixes() {
        if (this.hasPrefixes()) {
            return this.prefixes;
        }

        return EMPTY_PREFIX;
    }

    public boolean hasPermission(String permission) {
        return this.getAttachment().getPermissions().getOrDefault(permission.toLowerCase(), false);
    }

    public boolean hasPrefixes() {
        return this.prefixes != null;
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
        if (this.getGroups().isEmpty()) {
            Group defaultGroup = this.plugin.getPermissions().getDefaultGroup();
            if (defaultGroup != null) {
                this.addGroup(defaultGroup);
            }

            ClientPermissionStorage storage = this.plugin.getPermissions().getPermissionStorage();
            Group[] groups = storage.fetch(this.getPlayer());

            for (Group group : groups) {
                this.addGroup(group);
            }
        }

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

        this.refreshPrefixes();
    }

    public void refreshPrefixes() {
        StringBuilder builder = new StringBuilder();
        for (Group group : this.getGroups()) {
            if (group.hasPrefix()) {
                builder.append(group.getPrefix());
            }
        }

        this.setPrefixes(builder.toString());
    }

    public boolean removeGroup(Group group) {
        return this.groups.remove(group);
    }

    public void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }
}
