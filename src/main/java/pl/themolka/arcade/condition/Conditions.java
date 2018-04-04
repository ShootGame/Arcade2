package pl.themolka.arcade.condition;

import java.util.Collection;

public final class Conditions {
    private Conditions() {
    }

    public static <T> AllCondition<T> all(Collection<Condition<T, AbstainableResult>> all) {
        return new AllCondition<>(all);
    }

    public static <T> AnyCondition<T> any(Collection<Condition<T, AbstainableResult>> any) {
        return new AnyCondition<>(any);
    }

    public static <T> NoneCondition<T> none(Collection<Condition<T, AbstainableResult>> none) {
        return new NoneCondition<>(none);
    }

    public static <T, V extends InvertableResult<V>> NotCondition<T, V> not(Condition<T, V> not) {
        return new NotCondition<>(not);
    }
}
