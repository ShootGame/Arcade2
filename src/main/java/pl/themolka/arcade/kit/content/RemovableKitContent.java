package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.Removable;

public interface RemovableKitContent<T> extends KitContent<T>, Removable<GamePlayer> {
    @Override
    default void apply(GamePlayer player) {
        this.attach(player, this.getResult());
    }

    @Override
    default void remove(GamePlayer player) {
        this.attach(player, defaultValue());
    }

    void attach(GamePlayer player, T value);

    T defaultValue();

    interface Config<T extends RemovableKitContent<?>, R> extends KitContent.Config<T, R> {
    }
}
