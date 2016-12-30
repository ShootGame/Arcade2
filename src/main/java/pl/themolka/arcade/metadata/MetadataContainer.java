package pl.themolka.arcade.metadata;

import pl.themolka.arcade.module.Module;

import java.util.HashMap;
import java.util.Set;

public class MetadataContainer extends HashMap<String, Object> implements Metadata {
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
    public void setMetadata(Class<? extends Module<?>> owner, String key, Object metadata) {
        this.put(owner.getName() + Character.toString(KEY_SEPARATOR) + key, metadata);
    }
}
