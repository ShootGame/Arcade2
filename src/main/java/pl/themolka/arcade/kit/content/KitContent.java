package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.PlayerApplicable;

public interface KitContent<T> extends PlayerApplicable {
    default void applyIfApplicable(GamePlayer player) {
        if (this.isApplicable(player)) {
            this.apply(player);
        }
    }

    T getResult();

    default boolean isApplicable(GamePlayer player) {
        return test(player);
    }

    //
    // Player Testing Methods
    //

    static boolean test(GamePlayer player) {
        return player != null && player.isOnline();
    }

    static boolean testBukkit(GamePlayer player) {
        return test(player) && player.getBukkit() != null;
    }

    interface Config<T extends KitContent<?>, R> extends IGameConfig<T> {
        Ref<R> result();
    }
}
