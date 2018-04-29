package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class OrCondition<K> extends MultiCondition<K> {
    public OrCondition(Collection<Condition<K, AbstainableResult>> or) {
        super(or);
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
        return "OR(" + StringUtils.join(this.conditions, ", ") + ")";
    }
}
