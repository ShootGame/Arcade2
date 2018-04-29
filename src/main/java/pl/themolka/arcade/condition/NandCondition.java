package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class NandCondition<K, V extends InvertableResult<V>> extends NotCondition<K, V> {
    public NandCondition(Collection<Condition<K, V>> nand) {
        super(new AndCondition(nand));
    }

    @Override
    public AndCondition getCondition() {
        return (AndCondition) super.getCondition();
    }

    @Override
    public String toString() {
        return "NAND(" + StringUtils.join(this.getCondition().conditions, ", ") + ")";
    }
}
