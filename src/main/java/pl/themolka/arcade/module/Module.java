package pl.themolka.arcade.module;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.util.StringId;
import pl.themolka.arcade.util.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Module<T> extends SimpleModuleListener implements Listener, StringId {
    public static final String DEFAULT_VERSION_STRING = "1.0";
    public static final Version DEFAULT_VERSION = Version.valueOf(DEFAULT_VERSION_STRING);

    private ArcadePlugin plugin;

    private String id;
    private Class<? extends Module<?>>[] dependency;
    private Class<? extends Module<?>>[] loadBefore;
    private boolean global;
    private final List<Object> listenerObjects = new CopyOnWriteArrayList<>();
    private boolean loaded = false;
    private String name;
    private Version version = DEFAULT_VERSION;

    public Module() {
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
        if (info == null || info.id() == null || info.id().isEmpty()) {
            throw new RuntimeException("Module must be @ModuleInfo decorated");
        }

        this.id = info.id().toLowerCase();
        this.dependency = info.dependency();
        this.loadBefore = info.loadBefore();
        this.name = info.id();

        ModuleVersion versionInfo = this.getClass().getAnnotation(ModuleVersion.class);
        if (versionInfo != null && versionInfo.value() != null) {
            Version version = Version.valueOf(versionInfo.value());

            if (version != null) {
                this.setVersion(version);
            }
        }
    }

    @Override
    public final String getId() {
        return this.id;
    }

    public T buildGameModule(Element xml, Game game) throws JDOMException {
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

    public Game getGame() {
        return this.plugin.getGames().getCurrentGame();
    }

    public T getGameModule() {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            return (T) game.getModules().getModuleById(this.getId());
        }

        return null;
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

    public String getName() {
        return this.name;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public Version getVersion() {
        return this.version;
    }

    public boolean isGameModuleEnabled() {
        return this.getGame() != null && this.getGameModule() != null;
    }

    public boolean isGlobal() {
        return this.global;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void registerCommandObject(Object object) {
        this.plugin.registerCommandObject(object);
    }

    public boolean registerListenerObject(Object object) {
        boolean result = this.listenerObjects.add(object);
        if (result) {
            this.getPlugin().registerListenerObject(object);
        }

        return result;
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

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public void setLoadBefore(Class<? extends Module<?>>[] loadBefore) {
        this.loadBefore = loadBefore;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public boolean unregisterListenerObject(Object object) {
        this.getPlugin().unregisterListenerObject(object);
        return this.listenerObjects.remove(object);
    }
}
