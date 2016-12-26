package pl.themolka.arcade.map;

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
}
