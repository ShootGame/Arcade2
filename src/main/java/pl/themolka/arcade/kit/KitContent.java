package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.PlayerApplicable;

public interface KitContent<T> extends PlayerApplicable {
    T getResult();
}
