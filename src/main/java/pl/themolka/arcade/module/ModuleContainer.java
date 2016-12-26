package pl.themolka.arcade.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleContainer {
    private final Map<Class<? extends Module>, Module<?>> modulesByClass = new HashMap<>();
    private final Map<String, Module<?>> modulesById = new HashMap<>();

    public <T extends Module> T getModule(Class<? extends Module> module) {
        return (T) this.modulesByClass.get(module);
    }

    public <T extends Module> T getModule(String module) {
        return (T) this.modulesById.get(module);
    }

    public Collection<Module<?>> getModules() {
        return this.modulesByClass.values();
    }

    public Set<Class<? extends Module>> getModuleClasses() {
        return this.modulesByClass.keySet();
    }

    public Set<String> getModuleIds() {
        return this.modulesById.keySet();
    }

    public void register(Module<?>... modules) {
        for (Module<?> module : modules) {
            this.modulesByClass.put(module.getClass(), module);
            this.modulesById.put(module.getId(), module);
        }
    }
}
