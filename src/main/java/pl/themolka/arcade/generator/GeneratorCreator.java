package pl.themolka.arcade.generator;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.Propertable;

public interface GeneratorCreator<T extends Generator> {
    T create(ArcadePlugin plugin, Propertable properties);
}
