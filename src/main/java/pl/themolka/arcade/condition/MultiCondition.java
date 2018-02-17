package pl.themolka.arcade.condition;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class MultiCondition implements Condition {
    private final ImmutableList<Condition> conditions;

    public MultiCondition(Condition... conditions) {
        this(Arrays.asList(conditions));
    }

    public MultiCondition(Collection<Condition> conditions) {
        this.conditions = ImmutableList.copyOf(conditions);
    }

    @Override
    public boolean test() {
        for (Condition condition : this.conditions) {
            Result result = this.test(condition);

            if (!result.equals(Result.ABSTAIN)) {
                return result.toBoolean();
            }
        }

        return this.defaultValue();
    }

    public abstract boolean defaultValue();

    public List<Condition> getConditions() {
        return this.conditions;
    }

    public abstract Result test(Condition condition);

    enum Result {
        TRUE, FALSE, ABSTAIN;

        boolean toBoolean() {
            return this.equals(TRUE);
        }
    }
}
