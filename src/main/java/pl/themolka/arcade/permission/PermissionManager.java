package pl.themolka.arcade.permission;

import pl.themolka.arcade.ArcadePlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PermissionManager {
    private final ArcadePlugin plugin;

    private Group defaultGroup;
    private final Map<String, Group> groupsById = new HashMap<>();

    public PermissionManager(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    public void addGroup(Group group) {
        this.groupsById.put(group.getId(), group);
    }

    public Group getDefaultGroup() {
        return this.defaultGroup;
    }

    public Group getGroup(String groupId) {
        return this.groupsById.get(groupId);
    }

    public Set<String> getGroupIds() {
        return this.groupsById.keySet();
    }

    public Collection<Group> getGroups() {
        return this.groupsById.values();
    }

    public boolean hasGroup(String groupId) {
        return this.getGroup(groupId) != null;
    }

    public void removeGroup(Group group) {
        this.removeGroup(group.getId());
    }

    public void removeGroup(String groupId) {
        this.groupsById.remove(groupId);
    }

    public void setDefaultGroup(Group defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
