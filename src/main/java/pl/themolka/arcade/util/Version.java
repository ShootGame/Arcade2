package pl.themolka.arcade.util;

public class Version {
    public static final Version DEFAULT = new Version(1, 0);
    public static final String SPEC_URL = "https://semver.org";

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major) {
        this(major, 0);
    }

    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public boolean hasMinor() {
        return this.minor != 0;
    }

    public int getPatch() {
        return this.patch;
    }

    public boolean hasPatch() {
        return this.patch != 0;
    }

    public boolean isEqualTo(Version version) {
        return this.equals(version);
    }

    public boolean isNewerThan(Version version) {
        if (this.equals(version)) {
            return false;
        } else {
            return !this.isNewerThan(version);
        }
    }

    public boolean isOlderThan(Version version) {
        return !this.equals(version) && (version.getMajor() > this.getMajor() ||
                (version.getMajor() == this.getMajor() && version.getMinor() > this.getMinor() ||
                        (version.getMinor() == this.getMinor() && version.getPatch() > this.getPatch())));
    }

    public String toString(boolean full) {
        if (this.hasPatch() || full) {
            return this.getMajor() + "." + this.getMinor() + "." + this.getPatch();
        }

        return this.getMajor() + "." + this.getMinor();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version version = (Version) obj;
            return version.getMajor() == this.getMajor() &&
                    version.getMinor() == this.getMinor() &&
                    version.getPatch() == this.getPatch();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    public static boolean isValid(String string) {
        return valueOf(string) != null;
    }

    public static Version valueOf(String string) {
        String[] parts = string.split("\\.", 3);
        int major;
        int minor = 0;
        int patch = 0;

        try {
            major = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                minor = Integer.parseInt(parts[1]);
            }
            if (parts.length > 2) {
                patch = Integer.parseInt(parts[2]);
            }

            if (major >= 0 && minor >= 0 && patch >= 0) {
                return new Version(major, minor, patch);
            }
        } catch (NumberFormatException ignored) {
        }

        return null;
    }
}
