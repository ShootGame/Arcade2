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

package pl.themolka.arcade.game;

import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.util.Container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameModuleContainer implements Container<GameModule> {
    private final Map<Class<? extends Module<?>>, GameModule> modulesByClass = new HashMap<>();
    private final Map<String, GameModule> modulesById = new HashMap<>();

    @Override
    public Class<GameModule> getType() {
        return GameModule.class;
    }

    public boolean contains(Class<? extends Module<?>> clazz) {
        return this.getModuleClasses().contains(clazz);
    }

    public boolean contains(String id) {
        return this.getModuleIds().contains(id);
    }

    public boolean containsModule(GameModule module) {
        return this.getModules().contains(module);
    }

    public GameModule getModule(Class<? extends Module<?>> module) {
        return this.modulesByClass.get(module);
    }

    public GameModule getModuleById(String id) {
        return this.modulesById.get(id);
    }

    public Set<Class<? extends Module<?>>> getModuleClasses() {
        return this.modulesByClass.keySet();
    }

    public Set<String> getModuleIds() {
        return this.modulesById.keySet();
    }

    public Collection<GameModule> getModules() {
        return new ArrayList<>(this.modulesByClass.values());
    }

    public void register(GameModule... modules) {
        for (GameModule module : modules) {
            if (!this.containsModule(module)) {
                this.modulesByClass.put((Class<? extends Module<?>>) module.getModule().getClass(), module);
                this.modulesById.put(module.getModule().getId(), module);
            }
        }
    }

    public void register(GameModuleContainer container) {
        Collection<GameModule> modules = container.getModules();
        this.register(modules.toArray(new GameModule[modules.size()]));
    }
}
