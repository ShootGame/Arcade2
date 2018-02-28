package pl.themolka.arcade.game;

import pl.themolka.arcade.config.IConfig;

public interface IGameConfig<T> extends IConfig<T> {
    T create(Game game);
}
