package pl.themolka.arcade.filter;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.Condition;
import pl.themolka.arcade.game.IGameConfig;

/**
 * Something that can filter objects.
 */
public interface Filter extends Condition<Object[], AbstainableResult> {
    @Override
    default AbstainableResult query(Object[] objects) {
        return this.filter(objects);
    }

    AbstainableResult filter(Object... objects);

    interface Config<T extends Filter> extends IGameConfig<T> {
    }
}
