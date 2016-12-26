package pl.themolka.arcade.map;

import pl.themolka.arcade.util.Version;

public class MapVersion extends Version {
    public static final MapVersion DEFAULT = new MapVersion(1, 0);

    public MapVersion(int major) {
        super(major);
    }

    public MapVersion(int major, int minor) {
        super(major, minor);
    }

    public MapVersion(int major, int minor, int patch) {
        super(major, minor, patch);
    }

    @Override
    public boolean hasMinor() {
        return true;
    }

    public static MapVersion valueOf(String string) {
        return valueOf(Version.valueOf(string));
    }

    public static MapVersion valueOf(Version version) {
        return new MapVersion(version.getMajor(), version.getMinor(), version.getPatch());
    }
}
