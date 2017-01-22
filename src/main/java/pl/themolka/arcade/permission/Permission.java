package pl.themolka.arcade.permission;

import org.bukkit.ChatColor;
import pl.themolka.arcade.util.pagination.Paginationable;

import java.util.Objects;

public class Permission implements Paginationable {
    private final String name;
    private boolean revoke;

    public Permission(String name) {
        this(name, true);
    }

    public Permission(String name, boolean access) {
        this.name = name.toLowerCase();
        this.revoke = !access;
    }

    public Permission(Permission permission) {
        this(permission.getName(), permission.isAccess());
    }

    @Override
    public int compareTo(Paginationable o) {
        if (o instanceof Permission) {
            return this.getName().compareToIgnoreCase(((Permission) o).getName());
        }

        return this.getClass().getName().compareToIgnoreCase(o.getClass().getName());
    }

    @Override
    public String paginate(int index) {
        String text = ChatColor.RED + "#" + index + " " + ChatColor.YELLOW + this.getName();
        if (!this.isAccess()) {
            return text + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " (revoke)";
        }

        return text;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Permission && ((Permission) obj).getName().equals(this.getName());
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public boolean isAccess() {
        return !this.revoke;
    }

    public void setAccess(boolean access) {
        this.revoke = !access;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
