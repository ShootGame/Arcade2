package pl.themolka.arcade.config;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.map.WorldNameGenerator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConfigContext {
    private final Map<String, IConfig<?>> configMap = new LinkedHashMap<>();

    // TODO: We shouldn't use the world name generator.
    private final WorldNameGenerator idGenerator = new WorldNameGenerator();

    public boolean contains(IConfig<?> config) {
        return this.configMap.containsValue(config);
    }

    public boolean contains(String id) {
        return this.configMap.containsKey(id);
    }

    public IConfig<?> getConfig(String id) {
        return this.getConfig(id, null);
    }

    public IConfig<?> getConfig(String id, IConfig<?> def) {
        return this.configMap.getOrDefault(id, def);
    }

    public Collection<IConfig<?>> getConfigs() {
        return this.configMap.values();
    }

    public Set<String> getIds() {
        return this.configMap.keySet();
    }

    public boolean isEmpty() {
        return this.configMap.isEmpty();
    }

    public String nextUniqueId() {
        String uniqueId;
        do {
            uniqueId = StringUtils.substring(this.idGenerator.nextWorldName(), 0, 10);
        } while (this.configMap.containsKey(uniqueId));

        return uniqueId;
    }

    public boolean register(String id, IConfig<?> config) {
        return this.configMap.putIfAbsent(id, config) == null;
    }

    public boolean unregister(String id) {
        return this.configMap.remove(id) != null;
    }
}
