package pl.themolka.arcade.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleManager {
    private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

    public <T extends Module> T getModule(Class<? extends Module> module) {
        return (T) this.modules.get(module);
    }

    public Set<Class<? extends Module>> getModules() {
        return modules.keySet();
    }
}
