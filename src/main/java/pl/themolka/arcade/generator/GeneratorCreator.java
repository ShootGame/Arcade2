package pl.themolka.arcade.generator;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.Properties;

public interface GeneratorCreator<T> {
    T create(ArcadePlugin plugin, ArcadeMap map, Properties properties);
}
