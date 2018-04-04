package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class AnyCondition<K> extends MultiCondition<K> {
    public AnyCondition(Collection<Condition<K, AbstainableResult>> any) {
        super(any);
    }

    @Override
    public AbstainableResult defaultValue() {
        return OptionalResult.FALSE;
    }

    @Override
    public AbstainableResult query(K k, Condition<K, AbstainableResult> condition) {
        return condition.query(k).isTrue() ? OptionalResult.TRUE
                                           : OptionalResult.ABSTAIN;
    }

    @Override
    public String toString() {
        return "any(" + StringUtils.join(this.getConditions(), ", ") + ")";
    }
}
