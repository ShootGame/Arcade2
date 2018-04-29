package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class NorCondition<K, V extends InvertableResult<V>> extends NotCondition<K, V> {
    public NorCondition(Collection<Condition<K, V>> nor) {
        super(new OrCondition(nor));
    }

    @Override
    public OrCondition getCondition() {
        return (OrCondition) super.getCondition();
    }

    @Override
    public String toString() {
        return "NOR(" + StringUtils.join(this.getCondition().conditions, ", ") + ")";
    }
}
