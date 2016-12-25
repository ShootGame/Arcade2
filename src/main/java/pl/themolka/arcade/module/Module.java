package pl.themolka.arcade.module;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.ArcadePlugin;

import java.lang.annotation.Annotation;

public abstract class Module<T> {
    private ArcadePlugin plugin;

    private String id;
    private Class<? extends Module>[] dependency;
    private Class<? extends Module>[] loadBefore;

    private boolean loaded = false;

    public Module() {
    }

    public abstract T buildModule(Element xml) throws JDOMException;

    public Class<? extends Module>[] getDependency() {
        return this.dependency;
    }

    public String getId() {
        return this.id;
    }

    public Class<? extends Module>[] getLoadBefore() {
        return this.loadBefore;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public final void load(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        Annotation annotation = this.getClass().getAnnotation(ModuleInfo.class);
        if (annotation == null) {
            throw new RuntimeException("Module must be @ModuleInfo decorated");
        }

        ModuleInfo info = (ModuleInfo) annotation;
        this.id = info.id();
        this.dependency = info.dependency();
        this.loadBefore = info.loadBefore();

        this.registerCommandObject(this);
    }

    public void registerCommandObject(Object object) {
        this.plugin.registerCommandObject(object);
    }

    public void setDependency(Class<? extends Module>[] dependency) {
        this.dependency = dependency;
    }

    public void setLoadBefore(Class<? extends Module>[] loadBefore) {
        this.loadBefore = loadBefore;
    }
}
