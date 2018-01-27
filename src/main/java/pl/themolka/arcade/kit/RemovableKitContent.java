package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.GamePlayer;

public interface RemovableKitContent<T> extends KitContent<T>, Removable {
    @Override
    default void apply(GamePlayer player) {
        this.attach(player, this.getResult());
    }

    @Override
    default void revoke(GamePlayer player) {
        this.attach(player, defaultValue());
    }

    void attach(GamePlayer player, T value);

    T defaultValue();
}
