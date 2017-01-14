package pl.themolka.arcade.map;

import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OfflineMap {
    public static final int NAME_MAX_LENGTH = 32;

    private final String name;
    private final MapVersion version;
    private final String description;
    private final List<Author> authors = new ArrayList<>();

    private File directory;
    private File settings;

    public OfflineMap(String name, MapVersion version, String description, List<Author> authors) {
        this.name = name;
        this.version = version;
        this.description = description;

        if (authors != null) {
            this.authors.addAll(authors);
        }
    }

    public String getName() {
        return this.name;
    }

    public MapVersion getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Author> getAuthors() {
        return this.authors;
    }

    public String getAuthorsPretty() {
        if (this.hasAuthors()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < this.getAuthors().size(); i++) {
                if (i != 0) {
                    builder.append(ChatColor.GRAY);
                    if (this.getAuthors().size() == (i + 1)) {
                        builder.append(" and ");
                    } else {
                        builder.append(", ");
                    }
                }

                builder.append(ChatColor.DARK_PURPLE).append(this.getAuthors().get(i));
            }

            return builder.toString();
        }

        return ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "(unknown)";
    }

    public File getDirectory() {
        return this.directory;
    }

    public File getSettings() {
        return this.settings;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public boolean hasAuthors() {
        return !this.authors.isEmpty();
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setSettings(File settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return ChatColor.GOLD + this.getName() + ChatColor.GRAY + " v" + this.getVersion();
    }
}
