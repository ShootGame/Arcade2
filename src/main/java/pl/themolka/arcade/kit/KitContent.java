package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.Applicable;

public interface KitContent<T> extends Applicable<GamePlayer> {
    T getResult();
}
