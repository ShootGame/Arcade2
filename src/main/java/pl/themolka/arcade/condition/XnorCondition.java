package pl.themolka.arcade.condition;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class XnorCondition<K, V extends InvertableResult<V>> extends NotCondition<K, V> {
    public XnorCondition(Collection<Condition<K, V>> xnor) {
        super(new XorCondition(xnor));
    }

    @Override
    public XorCondition getCondition() {
        return (XorCondition) super.getCondition();
    }

    @Override
    public String toString() {
        return "XNOR(" + StringUtils.join(this.getCondition().conditions, ", ") + ")";
    }
}
