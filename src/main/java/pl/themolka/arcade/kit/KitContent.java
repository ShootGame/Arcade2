package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public interface KitContent<T> extends PlayerApplicable {
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
}
