/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.config;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.map.WorldNameGenerator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConfigContext {
    private final Map<String, IConfig> configMap = new LinkedHashMap<>();

    // TODO: We shouldn't use the world name generator.
    private final WorldNameGenerator idGenerator = new WorldNameGenerator();

    private final TypeFinder<Ref> refFinder = new TypeFinder<>(Ref.class);
    private final TypeFinder<IGameConfig> configFinder = new TypeFinder<>(IGameConfig.class);

    public ConfigContext() {
    }

    public boolean contains(IConfig config) {
        return this.configMap.containsValue(config);
    }

    public boolean define(IConfig config) {
        return this.define(null, config);
    }

    public boolean define(String id, IConfig config) {
        if (id == null && config instanceof Unique) {
            id = ((Unique) config).id();
        }

        return id != null && this.configMap.putIfAbsent(id, config) == null;
    }

    public boolean contains(String id) {
        return this.configMap.containsKey(id);
    }

    public IConfig getConfig(String id) {
        return this.getConfig(id, null);
    }

    public IConfig getConfig(String id, IConfig def) {
        return this.configMap.getOrDefault(id, def);
    }

    public Collection<IConfig> getConfigs() {
        return this.configMap.values();
    }

    public Set<String> getIds() {
        return this.configMap.keySet();
    }

    public TypeFinder<Ref> getRefFinder() {
        return this.refFinder;
    }

    public TypeFinder<IGameConfig> getConfigFinder() {
        return this.configFinder;
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

    public boolean unset(Unique unique) {
        return unique != null && this.unset(unique.id());
    }

    public boolean unset(String id) {
        return id != null && this.configMap.remove(id) != null;
    }
}
