package pl.themolka.arcade.map;

import pl.themolka.arcade.util.versioning.Version;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Changelog<T extends Version<T>> extends ArrayList<String> {
    private final T version;
    private LocalDate release;

    public Changelog(T version) {
        this.version = version;
    }

    public Changelog(T version, LocalDate release) {
        this(version);

        this.release = release;
    }

    public T getVersion() {
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
