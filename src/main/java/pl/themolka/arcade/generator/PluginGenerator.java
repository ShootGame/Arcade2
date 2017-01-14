package pl.themolka.arcade.generator;

import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Properties;

public class PluginGenerator implements Generator {
    private final String id;
    private final Plugin plugin;
    private final String world;

    private PluginGenerator(Plugin plugin, String world, String id) {
        this.id = id;
        this.plugin = plugin;
        this.world = world;
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return this.getPlugin().getDefaultWorldGenerator(this.getWorld(), this.getGeneratorId());
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.CUSTOMIZED;
    }

    public String getGeneratorId() {
        return this.id;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getWorld() {
        return this.world;
    }

    public static class Parser implements GeneratorCreator<PluginGenerator> {
        @Override
        public PluginGenerator create(ArcadePlugin plugin, ArcadeMap map, Properties properties) {
            return new PluginGenerator(this.getPlugin(plugin, properties), map.getWorldName(), properties.getProperty("id"));
        }

        private Plugin getPlugin(ArcadePlugin arcade, Properties properties) {
            String plugin = properties.getProperty("plugin");
            if (plugin != null) {
                return arcade.getServer().getPluginManager().getPlugin(plugin);
            }

            return null;
        }
    }
}
