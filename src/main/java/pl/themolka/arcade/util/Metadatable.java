package pl.themolka.arcade.util;

import java.util.Set;

public interface Metadatable {
    default Object getMetadata(String key) {
        return this.getMetadata(key, null);
    }

    Object getMetadata(String key, Object def);

    Set<String> getMetadataKeys();

    default boolean hasMetadata(String key) {
        return this.getMetadata(key) != null;
    }

    void setMetadata(String key, Object def);
}
