package pl.themolka.arcade.permission;

import org.jdom2.Element;
import pl.themolka.arcade.util.StringId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Group implements StringId {
    private final String id;
    private boolean def;
    private final Element element;
    private String name;
    private boolean operator;
    private final Map<String, Permission> permissions = new HashMap<>();
    private String prefix;

    public Group(String id) {
        this(id, null);
    }

    public Group(String id, Element element) {
        this.id = id;
        this.element = element;
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

    public void addPermission(String permission, boolean grant) {
        this.addPermission(new Permission(permission), grant);
    }

    public void addPermission(Permission permission, boolean grant) {
        permission.setAccess(grant);
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

    public Element getElement() {
        return this.element;
    }

    public String getName() {
        return this.name;
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

    public boolean hasPrefix() {
        return this.prefix != null;
    }

    public boolean isDefault() {
        return this.def;
    }

    public boolean isOperator() {
        return this.operator;
    }

    public void removePermission(String permission) {
        this.removePermission(new Permission(permission));
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission.getName());
    }

    public void setDefault(boolean def) {
        this.def = def;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
