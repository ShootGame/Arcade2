package pl.themolka.arcade.util.versioning;

import org.apache.commons.lang3.Validate;

import java.util.Objects;

public class SemanticVersion extends Version.Impl<SemanticVersion> {
    public static final String MANUAL = "https://semver.org/";
    public static final SemanticVersion DEFAULT = new SemanticVersion(1, 0, 0);

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean pre;

    public SemanticVersion(int major, int minor) {
        this(major, minor, DEFAULT.isPre());
    }

    public SemanticVersion(int major, int minor, boolean pre) {
        this(major, minor, DEFAULT.patch, pre);
    }

    public SemanticVersion(int major, int minor, int patch) {
        this(major, minor, patch, DEFAULT.isPre());
    }

    public SemanticVersion(int major, int minor, int patch, boolean pre) {
        Validate.isTrue(major >= 0, "major cannot be negative");
        Validate.isTrue(minor >= 0, "minor cannot be negative");
        Validate.isTrue(patch >= 0, "patch cannot be negative");

        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.pre = pre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticVersion that = (SemanticVersion) o;
        return major == that.major &&
                minor == that.minor &&
                patch == that.patch &&
                pre == that.pre;
    }

    @Override
    public boolean isOldenThan(SemanticVersion than) {
        if (this.equals(than) || !Objects.equals(this.pre, Objects.requireNonNull(than, "than cannot be null").pre)) {
            return false;
        }

        return this.major <= than.major || this.minor <= than.minor || this.patch < than.patch;
    }

    @Override
    public boolean isNewerThan(SemanticVersion than) {
        if (this.equals(than) || !Objects.equals(this.pre, Objects.requireNonNull(than, "than cannot be null").pre)) {
            return false;
        }

        return this.major >= than.major || this.minor >= than.minor || this.patch > than.patch;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, pre);
    }

    public boolean isBeta() {
        return this.major == 0;
    }

    public boolean isPre() {
        return this.pre;
    }

    public boolean isUnstable() {
        return this.isBeta() || this.isPre();
    }

    @Override
    public SemanticVersion previous() throws NoPreviousException {
        if (this.patch == DEFAULT.patch) {
            throw new NoPreviousException();
        }

        return new SemanticVersion(this.major, this.minor, this.patch - 1, false);
    }

    @Override
    public SemanticVersion next() throws NoNextException {
        return new SemanticVersion(this.major, this.minor, this.patch + 1, true);
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean full) {
        String value = this.major + "." + this.minor;
        if (full || this.patch != DEFAULT.patch) {
            return value + "." + this.patch;
        }

        return value;
    }
}
