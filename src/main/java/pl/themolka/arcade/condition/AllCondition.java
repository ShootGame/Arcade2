package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class AllCondition<K> extends MultiCondition<K> {
    public AllCondition(Collection<Condition<K, AbstainableResult>> all) {
        super(all);
    }

    @Override
    public AbstainableResult defaultValue() {
        return OptionalResult.TRUE;
    }

    @Override
    public AbstainableResult query(K k, Condition<K, AbstainableResult> condition) {
        AbstainableResult result = condition.query(k);
        return result.isNotTrue() ? OptionalResult.FALSE
                                  : OptionalResult.ABSTAIN;
    }

    @Override
    public String toString() {
        return "all(" + StringUtils.join(this.getConditions(), ", ") + ")";
    }
}
