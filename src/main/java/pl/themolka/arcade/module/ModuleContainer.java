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

package pl.themolka.arcade.module;

import pl.themolka.arcade.util.Container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleContainer implements Container<Module> {
    private final Map<Class<? extends Module>, Module<?>> modulesByClass = new HashMap<>();
    private final Map<String, Module<?>> modulesById = new HashMap<>();

    @Override
    public Class<Module> getType() {
        return Module.class;
    }

    public boolean contains(Class<? extends Module> clazz) {
        return this.getModuleClasses().contains(clazz);
    }

    public boolean contains(String id) {
        return this.getModuleIds().contains(id);
    }

    public boolean containsModule(Module<?> module) {
        return this.getModules().contains(module);
    }

    public <T extends Module> T getModule(Class<T> module) {
        return (T) this.modulesByClass.get(module);
    }

    public Module getModuleById(String id) {
        return this.modulesById.get(id);
    }

    public Set<Class<? extends Module>> getModuleClasses() {
        return this.modulesByClass.keySet();
    }

    public Set<String> getModuleIds() {
        return this.modulesById.keySet();
    }

    public Collection<Module<?>> getModules() {
        return this.modulesByClass.values();
    }

    public void register(Module<?>... modules) {
        for (Module<?> module : modules) {
            if (!this.containsModule(module)) {
                this.modulesByClass.put(module.getClass(), module);
                this.modulesById.put(module.getId(), module);
            }
        }
    }

    public void register(ModuleContainer container) {
        Collection<Module<?>> modules = container.getModules();
        this.register(modules.toArray(new Module<?>[modules.size()]));
    }
}
