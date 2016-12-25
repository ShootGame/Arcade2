package pl.themolka.arcade;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.arcade.module.Module;
import pl.themolka.commons.BukkitCommonsFactory;
import pl.themolka.commons.Commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The Arcade main class
 */
public final class ArcadePlugin extends JavaPlugin {
    private Commons commons;

    @Override
    public void onLoad() {
        this.commons = BukkitCommonsFactory.bukkitFactory(this).build(); // TODO custom
    }

    @Override
    public void onEnable() {
        List<Module<?>> modules = this.loadModules();
        for (Module<?> module : modules) {
            module.load(this);
        }
    }

    @Override
    public void onDisable() {
        // TODO body
    }

    public Commons getCommons() {
        return this.commons;
    }

    public void registerCommandObject(Object object) {
        this.getCommons().getCommands().registerCommandObject(this);
    }

    public void registerListenerObject(Object object) {
        this.getCommons().getEvents().register(object);

        if (object instanceof Listener) {
            this.getServer().getPluginManager().registerEvents((Listener) object, this);
        }
    }

    public void unregisterListenerObject(Object object) {
        this.getCommons().getEvents().unregister(object);

        if (object instanceof Listener) {
            HandlerList.unregisterAll((Listener) object);
        }
    }

    private Module<?> getXmlModule(Element module) throws ReflectiveOperationException {
        String clazzPath = module.getAttributeValue("class");

        Class<?> clazz = Class.forName(clazzPath);
        if (clazz.isAssignableFrom(Module.class)) {
            return (Module<?>) clazz.newInstance();
        }

        return null;
    }

    private List<Module<?>> loadModules() {
        List<Module<?>> modules = new ArrayList<>();

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream("modules.xml")){
            Document document = new SAXBuilder().build(input);

            for (Element xml : document.getRootElement().getChildren("module")) {
                try {
                    modules.add(this.getXmlModule(xml));
                } catch (ReflectiveOperationException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException | JDOMException ex) {
            ex.printStackTrace();
        }

        return modules;
    }
}
