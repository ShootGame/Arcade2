package pl.themolka.arcade.condition;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

public abstract class MultiCondition<T> implements Condition<T> {
    private final ImmutableList<Condition<T>> conditions;

    public MultiCondition(Collection<Condition<T>> conditions) {
        this.conditions = ImmutableList.copyOf(conditions);
    }

    @Override
    public boolean test(T t) {
        for (Condition<T> condition : this.conditions) {
            Result result = this.test(t, condition);

            if (!result.equals(Result.ABSTAIN)) {
                return result.toBoolean();
            }
        }

        return this.defaultValue();
    }

    public abstract boolean defaultValue();

    public List<Condition<T>> getConditions() {
        return this.conditions;
    }

    public abstract Result test(T t, Condition<T> condition);

    enum Result {
        TRUE, FALSE, ABSTAIN;

        boolean toBoolean() {
            return this.equals(TRUE);
        }
    }
}
