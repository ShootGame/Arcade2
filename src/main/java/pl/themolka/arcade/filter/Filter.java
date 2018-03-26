package pl.themolka.arcade.filter;

import pl.themolka.arcade.game.IGameConfig;

public interface Filter {
    FilterResult filter(Object... objects);

    interface Config<T extends Filter> extends IGameConfig<T> {
    }
}
