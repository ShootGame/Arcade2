package pl.themolka.arcade.map;

import pl.themolka.arcade.util.versioning.SemanticVersion;

public class MapFileVersion extends SemanticVersion {
    public MapFileVersion(int major, int minor) {
        super(major, minor);
    }

    public MapFileVersion(int major, int minor, boolean pre) {
        super(major, minor, pre);
    }

    public MapFileVersion(int major, int minor, int patch) {
        super(major, minor, patch);
    }

    public MapFileVersion(int major, int minor, int patch, boolean pre) {
        super(major, minor, patch, pre);
    }

    @Override
    public SemanticVersion previous() throws NoPreviousException {
        throw new NoPreviousException();
    }

    @Override
    public SemanticVersion next() throws NoNextException {
        throw new NoNextException();
    }

    public static MapFileVersion convertFrom(SemanticVersion semantic) {
        return new MapFileVersion(semantic.getMajor(), semantic.getMinor(), semantic.getPatch(), semantic.isPre());
    }
}
