package pl.themolka.arcade.metadata;

import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.util.Container;

import java.util.HashMap;
import java.util.Set;

/**
 * Simple implementation of the {@link Metadata} class which stores the data in a {@link HashMap}.
 */
public class MetadataContainer extends HashMap<String, Object> implements Container<Object>, Metadata {
    /**
     * A key separating a owner module class and a >custom key of the metadata.
     */
    public static final char KEY_SEPARATOR = '/';

    @Override
    public Object getMetadata(Class<? extends Module<?>> owner, String key, Object def) {
        return this.getOrDefault(owner.getName() + Character.toString(KEY_SEPARATOR) + key, def);
    }

    @Override
    public Set<String> getMetadataKeys() {
        return this.keySet();
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    @Override
    public void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata) {
        String fullKey = owner.getName() + Character.toString(KEY_SEPARATOR) + key;

        if (metadata != null) {
            this.put(fullKey, metadata);
        } else {
            this.remove(fullKey);
        }
    }
}
