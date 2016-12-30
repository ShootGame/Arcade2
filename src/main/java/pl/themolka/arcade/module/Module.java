package pl.themolka.arcade.module;

import org.bukkit.event.Listener;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.ArcadePlugin;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Module<T> extends SimpleModuleListener implements Listener, Serializable {
    private transient ArcadePlugin plugin;

    private String id;
    private Class<? extends Module<?>>[] dependency;
    private Class<? extends Module<?>>[] loadBefore;
    private final transient List<Object> listenerObjects = new CopyOnWriteArrayList<>();
    private boolean loaded = false;

    public Module() {
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        Annotation annotation = this.getClass().getAnnotation(ModuleInfo.class);
        if (annotation == null) {
            throw new RuntimeException("Module must be @ModuleInfo(id = ?) decorated");
        }

        ModuleInfo info = (ModuleInfo) annotation;
        if (info.id() == null) {
            throw new RuntimeException("Module must be @ModuleInfo(id = ?) decorated");
        }

        this.id = info.id().toLowerCase();
        this.dependency = info.dependency();
        this.loadBefore = info.loadBefore();
    }

    public T buildGameModule(Element xml) throws JDOMException {
        return null;
    }

    public final void destroy() {
        this.unregisterListenerObject(this);

        for (Object listener : this.getListenerObjects()) {
            this.unregisterListenerObject(listener);
        }
    }

    public Class<? extends Module<?>>[] getDependency() {
        return this.dependency;
    }

    public final String getId() {
        return this.id;
    }

    public List<Object> getListenerObjects() {
        return this.listenerObjects;
    }

    public Class<? extends Module<?>>[] getLoadBefore() {
        return this.loadBefore;
    }

    public Logger getLogger() {
        return this.getPlugin().getLogger();
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void registerCommandObject(Object object) {
        this.plugin.registerCommandObject(object);
    }

    public void registerListenerObject(Object object) {
        if (this.listenerObjects.add(object)) {
            this.getPlugin().registerListenerObject(object);
        }
    }

    public void registerListeners() {
        if (!this.getListenerObjects().isEmpty()) {
            return;
        }

        List<Object> listeners = this.onListenersRegister(new ArrayList<>());
        if (listeners != null) {
            for (Object listener : listeners) {
                this.registerListenerObject(listener);
            }
        }

        this.registerListenerObject(this);
    }

    public void setDependency(Class<? extends Module<?>>[] dependency) {
        this.dependency = dependency;
    }

    public void setLoadBefore(Class<? extends Module<?>>[] loadBefore) {
        this.loadBefore = loadBefore;
    }

    public boolean unregisterListenerObject(Object object) {
        this.getPlugin().unregisterListenerObject(object);
        return this.listenerObjects.remove(object);
    }
}
