package pl.themolka.arcade.condition;

import java.util.function.Predicate;

public interface Condition<K, V extends ConditionResult> extends Predicate<K> {
    @Override
    default boolean test(K k) {
        return this.query(k).toBoolean();
    }

    V query(K k);
}
