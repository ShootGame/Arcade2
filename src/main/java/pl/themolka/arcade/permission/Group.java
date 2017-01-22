package pl.themolka.arcade.permission;

import pl.themolka.arcade.util.StringId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Group implements StringId {
    private final String id;
    private boolean operator;
    private final Map<String, Permission> permissions = new HashMap<>();
    private String prefix;

    public Group(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void addParent(Group group) {
        this.addPermissions(group.getPermissions().toArray(new Permission[group.getPermissions().size()]));
    }

    public void addPermission(String permission) {
        this.addPermission(new Permission(permission));
    }

    public void addPermission(Permission permission) {
        this.addPermission(permission, permission.isAccess());
    }

    public void addPermission(String permission, boolean access) {
        this.addPermission(new Permission(permission), access);
    }

    public void addPermission(Permission permission, boolean access) {
        permission.setAccess(access);
        this.permissions.put(permission.getName(), permission);
    }

    public void addPermissions(Permission... permissions) {
        for (Permission permission : permissions) {
            this.addPermission(permission);
        }
    }

    public boolean checkPermission(String permission) {
        return this.isOperator() || (this.contains(permission) && this.permissions.get(permission.toLowerCase()).isAccess());
    }

    public boolean checkPermission(Permission permission) {
        return this.checkPermission(permission.getName());
    }

    public boolean contains(String permission) {
        return this.permissions.containsKey(permission.toLowerCase());
    }

    public boolean contains(Permission permission) {
        return this.contains(permission.getName());
    }

    public Set<String> getPermissionNames() {
        return this.permissions.keySet();
    }

    public Collection<Permission> getPermissions() {
        return this.permissions.values();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isOperator() {
        return this.operator;
    }

    public void removePermission(String permission) {
        this.removePermission(new Permission(permission));
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
