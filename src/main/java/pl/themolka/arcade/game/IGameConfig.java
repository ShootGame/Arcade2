package pl.themolka.arcade.game;

import pl.themolka.arcade.config.IConfig;

public interface IGameConfig<T> extends IConfig<T> {
    /**
     * Create a new T object based on this config.
     * @param game The game this object is created in.
     * @return The object based on this config, or {@code null}.
     */
    T create(Game game);
}
