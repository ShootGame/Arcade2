package pl.themolka.arcade.map;

import pl.themolka.arcade.util.Version;

public class MapFileVersion extends Version {
    public static final MapFileVersion DEFAULT = MapFileVersions.NEWEST;

    public MapFileVersion(int major) {
        super(major);
    }

    public MapFileVersion(int major, int minor) {
        super(major, minor);
    }

    public MapFileVersion(int major, int minor, int patch) {
        super(major, minor, patch);
    }

    @Override
    public boolean hasMinor() {
        return true;
    }

    public static MapFileVersion valueOf(String string) {
        Version version = Version.valueOf(string);
        if (version != null) {
            return valueOf(version);
        }

        return null;
    }

    public static MapFileVersion valueOf(Version version) {
        return new MapFileVersion(version.getMajor(), version.getMinor(), version.getPatch());
    }
}
