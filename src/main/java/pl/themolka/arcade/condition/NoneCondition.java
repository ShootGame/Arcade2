package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class NoneCondition<K> extends OrCondition<K> {
    public NoneCondition(Collection<Condition<K, AbstainableResult>> none) {
        super(none);
    }

    @Override
    public AbstainableResult defaultValue() {
        return OptionalResult.TRUE;
    }

    @Override
    public AbstainableResult query(K k, Condition<K, AbstainableResult> condition) {
        return condition.query(k).isTrue() ? OptionalResult.FALSE
                                           : OptionalResult.ABSTAIN;
    }

    @Override
    public String toString() {
        return "NONE(" + StringUtils.join(this.conditions, ", ") + ")";
    }
}
