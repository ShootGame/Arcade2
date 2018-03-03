package pl.themolka.arcade.condition;

import java.util.Collection;

public final class Conditions {
    private Conditions() {
    }

    public static <T> AllCondition<T> all(Collection<Condition<T>> all) {
        return new AllCondition<>(all);
    }

    public static <T> AnyCondition<T> any(Collection<Condition<T>> any) {
        return new AnyCondition<>(any);
    }

    public static <T> NoneCondition<T> none(Collection<Condition<T>> none) {
        return new NoneCondition<>(none);
    }

    public static <T> NotCondition<T> not(Condition<T> not) {
        return new NotCondition<>(not);
    }
}
