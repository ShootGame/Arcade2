package pl.themolka.arcade.condition;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

public abstract class MultiCondition<K> implements Condition<K, AbstainableResult> {
    private final List<Condition<K, AbstainableResult>> conditions;

    public MultiCondition(Collection<Condition<K, AbstainableResult>> conditions) {
        this.conditions = ImmutableList.copyOf(conditions);
    }

    @Override
    public AbstainableResult query(K k) {
        for (Condition<K, AbstainableResult> condition : this.conditions) {
            AbstainableResult result = this.query(k, condition);

            if (result.isNotAbstaining()) {
                return result;
            }
        }

        return this.defaultValue();
    }

    public abstract AbstainableResult defaultValue();

    public List<Condition<K, AbstainableResult>> getConditions() {
        return this.conditions;
    }

    public abstract AbstainableResult query(K k, Condition<K, AbstainableResult> condition);
}
