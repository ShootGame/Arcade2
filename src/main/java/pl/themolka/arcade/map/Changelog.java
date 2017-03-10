package pl.themolka.arcade.map;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Changelog extends ArrayList<String> {
    private final MapVersion version;
    private LocalDate release;

    public Changelog(MapVersion version) {
        this.version = version;
    }

    public Changelog(MapVersion version, LocalDate release) {
        this(version);

        this.release = release;
    }

    public MapVersion getVersion() {
        return this.version;
    }

    public LocalDate getRelease() {
        return this.release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }

    public static LocalDate parseRelease(String query) throws DateTimeParseException {
        if (query != null) {
            return LocalDate.parse(query);
        }

        return null;
    }
}
