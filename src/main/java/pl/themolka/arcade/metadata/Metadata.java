package pl.themolka.arcade.metadata;

import pl.themolka.arcade.module.Module;

import java.util.Set;

public interface Metadata {
    default Object getMetadata(Class<? extends Module<?>> owner, String key) {
        return this.getMetadata(owner, key, null);
    }

    Object getMetadata(Class<? extends Module<?>> owner, String key, Object def);

    Set<String> getMetadataKeys();

    default boolean hasMetadata(Class<? extends Module<?>> owner, String key) {
        return this.getMetadata(owner, key) != null;
    }

    void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata);

    default void removeMetadata(Class<? extends Module<?>> owner, String key) {
        this.setMetadata(owner, key, null);
    }
}
