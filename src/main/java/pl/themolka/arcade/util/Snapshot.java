package pl.themolka.arcade.util;

public interface Snapshot<T> extends Container<T> {
    Class<T> getSnapshotType();

    default Class<T> getType() {
        return this.getSnapshotType();
    }
}
