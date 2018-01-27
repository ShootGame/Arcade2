package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public interface Removable extends PlayerApplicable {
    @Override
    default void apply(GamePlayer player) {
        this.revoke(player);
    }

    void revoke(GamePlayer player);
}
