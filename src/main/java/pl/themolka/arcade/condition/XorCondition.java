package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class XorCondition<K> extends MultiCondition<K> {
    public XorCondition(Collection<Condition<K, AbstainableResult>> xor) {
        super(xor);
    }

    @Override
    public AbstainableResult query(K k) {
        ConditionResult last = null;
        for (Condition<K, AbstainableResult> condition : this.conditions) {
            AbstainableResult result = condition.query(k);
            if (result.isAbstaining()) {
                continue;
            }

            if (last != null && !last.equals(result)) {
                return OptionalResult.FALSE;
            }
            last = result;
        }

        return OptionalResult.TRUE;
    }

    @Override
    public String toString() {
        return "XOR(" + StringUtils.join(this.conditions, ", ") + ")";
    }

    //
    // Unused Methods
    //

    @Override
    public final AbstainableResult defaultValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final AbstainableResult query(K k, Condition<K, AbstainableResult> condition) {
        throw new UnsupportedOperationException();
    }
}
