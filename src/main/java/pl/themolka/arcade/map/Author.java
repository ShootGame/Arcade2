package pl.themolka.arcade.map;

import org.bukkit.ChatColor;

import java.util.UUID;

public class Author {
    private final UUID uuid;
    private final String username;
    private final String description;

    public Author(String username) {
        this(null, username);
    }

    public Author(String username, String description) {
        this(null, username, description);
    }

    public Author(UUID uuid, String username, String description) {
        this.uuid = uuid;
        this.username = username;
        this.description = description;
    }

    public UUID getUuid() {
        return this.uuid;
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
        String author = ChatColor.GRAY + " - " + ChatColor.GOLD + this.getUsername() + ChatColor.RESET;
        if (!this.hasDescription()) {
            return author;
        }

        return author + ChatColor.GRAY + " - " + ChatColor.ITALIC + this.getDescription() + ChatColor.RESET;
    }
}
