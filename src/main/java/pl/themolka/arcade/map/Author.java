package pl.themolka.arcade.map;

import org.bukkit.ChatColor;

import java.util.UUID;

public class Author implements Comparable<Author> {
    private final UUID uuid;
    private final UUID offlineUuid;
    private final String username;
    private final String description;

    public Author(String username) {
        this((UUID) null, username);
    }

    public Author(String username, String description) {
        this(null, username, description);
    }

    public Author(UUID uuid, String username) {
        this(uuid, username, null);
    }

    public Author(UUID uuid, String username, String description) {
        this.uuid = uuid;
        this.offlineUuid = this.newOfflineUuid(username);
        this.username = username;
        this.description = description;
    }

    @Override
    public int compareTo(Author o) {
        return this.getUsername().compareToIgnoreCase(o.getUsername());
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getOfflineUuid() {
        if (this.hasUuid()) {
            return this.offlineUuid;
        }

        return null;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasUuid() {
        return this.uuid != null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public boolean hasUsername() {
        return this.username != null;
    }

    @Override
    public String toString() {
        if (this.hasUsername()) {
            String author = ChatColor.GOLD + this.getUsername() + ChatColor.RESET;
            if (!this.hasDescription()) {
                return author;
            }

            return author + ChatColor.GRAY + " - " + ChatColor.ITALIC + this.getDescription() + ChatColor.RESET;
        }

        return null;
    }

    private UUID newOfflineUuid(String username) {
        if (username != null) {
            String offline = "OfflinePlayer:" + username;
            return UUID.nameUUIDFromBytes(offline.getBytes());
        }

        return null;
    }
}
