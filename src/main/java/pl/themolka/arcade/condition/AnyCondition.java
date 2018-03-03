package pl.themolka.arcade.condition;

import java.util.Collection;

public class AnyCondition<T> extends MultiCondition<T> {
    public AnyCondition(Collection<Condition<T>> any) {
        super(any);
    }

    @Override
    public boolean defaultValue() {
        return false;
    }

    @Override
    public Result test(T t, Condition<T> condition) {
        return condition.test(t) ? Result.TRUE : Result.ABSTAIN;
    }
}
