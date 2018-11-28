package pl.themolka.arcade.map;

import org.bukkit.ChatColor;
import pl.themolka.arcade.util.pagination.Paginationable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OfflineMap implements Paginationable {
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 32;

    private final MapFileVersion fileVersion;
    private final String name;
    private final MapVersion version;
    private final String description;
    private final List<Author> authors = new ArrayList<>();
    private final Map<MapVersion, Changelog> changelogMap = new TreeMap<>();

    private File directory;
    private File settings;

    public OfflineMap(MapFileVersion fileVersion, String name, MapVersion version,
                      String description, List<Author> authors, List<Changelog> changelogs) {
        this.fileVersion = fileVersion;
        this.name = name;
        this.version = version;
        this.description = description;

        if (authors != null) {
            this.authors.addAll(authors);
            Collections.sort(this.authors);
        }

        if (changelogs != null) {
            for (Changelog changelog : changelogs) {
                this.addChangelog(changelog);
            }
        }
    }

    @Override
    public int compareTo(Paginationable object) {
        if (object instanceof OfflineMap) {
            return this.getName().compareToIgnoreCase(
                    ((OfflineMap) object).getName());
        }

        return this.toString().compareToIgnoreCase(object.toString());
    }

    @Override
    public String paginate(int index) {
        String authorsString = "";
        if (this.hasAuthors()) {
            authorsString = ChatColor.GRAY + " by " + this.getAuthorsPretty();
        }

        return ChatColor.GRAY + "#" + index + " " + ChatColor.AQUA +
                ChatColor.BOLD + this.getName() + ChatColor.RESET +
                ChatColor.GRAY + " v" + ChatColor.AQUA +
                this.getVersion() + authorsString;
    }

    public boolean addChangelog(Changelog changelog) {
        boolean result = !this.hasChangelog(changelog);
        if (result) {
            this.changelogMap.put(changelog.getVersion(), changelog);
        }

        return result;
    }

    public MapFileVersion getFileVersion() {
        return this.fileVersion;
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
        return new ArrayList<>(this.authors);
    }

    public String getAuthorsPretty() {
        return this.getAuthorsPretty(ChatColor.AQUA, ChatColor.GRAY);
    }

    public String getAuthorsPretty(ChatColor primary, ChatColor secondary) {
        if (this.hasAuthors()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < this.getAuthors().size(); i++) {
                if (i != 0) {
                    builder.append(secondary);
                    if (this.getAuthors().size() == (i + 1)) {
                        builder.append(" and ");
                    } else {
                        builder.append(", ");
                    }
                }

                Author author = this.getAuthors().get(i);
                builder.append(primary).append(author.getUsername());
            }

            return builder.toString();
        }

        return primary + ChatColor.ITALIC.toString() + "(unknown)";
    }

    public Changelog getChangelog() {
        return this.getChangelog(this.getVersion());
    }

    public Changelog getChangelog(MapVersion version) {
        return this.changelogMap.get(version);
    }

    public Collection<Changelog> getChangelogs() {
        return this.changelogMap.values();
    }

    public File getDirectory() {
        return this.directory;
    }

    public File getSettings() {
        return this.settings;
    }

    public boolean hasChangelog() {
        return this.hasChangelog(this.getVersion());
    }

    public boolean hasChangelog(Changelog changelog) {
        return this.hasChangelog(changelog.getVersion());
    }

    public boolean hasChangelog(MapVersion version) {
        return this.getChangelog(version) != null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public boolean hasAuthors() {
        return !this.authors.isEmpty();
    }

    public boolean removeChangelog(Changelog changelog) {
        return this.removeChangelog(changelog.getVersion());
    }

    public boolean removeChangelog(MapVersion version) {
        boolean result = this.hasChangelog(version);
        if (result) {
            this.changelogMap.remove(version);
        }

        return result;
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
