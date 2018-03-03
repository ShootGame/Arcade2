package pl.themolka.arcade.condition;

import java.util.Collection;

public class NoneCondition<T> extends AnyCondition<T> {
    public NoneCondition(Collection<Condition<T>> none) {
        super(none);
    }

    @Override
    public boolean defaultValue() {
        return true;
    }

    @Override
    public Result test(T t, Condition<T> condition) {
        return condition.test(t) ? Result.FALSE : Result.ABSTAIN;
    }
}
