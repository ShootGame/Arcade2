package pl.themolka.arcade.map;

import org.bukkit.ChatColor;
import pl.themolka.arcade.util.pagination.Paginationable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfflineMap implements Paginationable {
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
            Collections.sort(this.authors);
        }
    }

    @Override
    public int compareTo(Paginationable object) {
        if (object instanceof OfflineMap) {
            return this.getName().compareToIgnoreCase(((OfflineMap) object).getName());
        }

        return this.getClass().getName().compareToIgnoreCase(object.getClass().getName());
    }

    @Override
    public String paginate(int index) {
        String authorsString = "";
        if (this.hasAuthors()) {
            authorsString = ChatColor.GRAY + " by " + this.getAuthorsPretty();
        }

        return ChatColor.GRAY + "#" + index + " " + ChatColor.AQUA + ChatColor.BOLD + this.getName() + ChatColor.RESET +
                ChatColor.GRAY + " v" + ChatColor.AQUA + this.getVersion() + authorsString;
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

                builder.append(ChatColor.AQUA).append(this.getAuthors().get(i).getUsername());
            }

            return builder.toString();
        }

        return ChatColor.AQUA + ChatColor.ITALIC.toString() + "(unknown)";
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
