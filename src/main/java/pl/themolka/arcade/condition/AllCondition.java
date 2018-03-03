package pl.themolka.arcade.condition;

import java.util.Collection;

public class AllCondition<T> extends MultiCondition<T> {
    public AllCondition(Collection<Condition<T>> all) {
        super(all);
    }

    @Override
    public boolean defaultValue() {
        return true;
    }

    @Override
    public Result test(T t, Condition<T> condition) {
        return condition.test(t) ? Result.ABSTAIN : Result.FALSE;
    }
}
