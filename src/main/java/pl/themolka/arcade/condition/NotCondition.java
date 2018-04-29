package pl.themolka.arcade.condition;

public class NotCondition<K, V extends InvertableResult<V>> extends SingleCondition<K, V> {
    public NotCondition(Condition<K, V> condition) {
        super(condition);
    }

    @Override
    public V query(K k, Condition<K, V> condition) {
        return condition.query(k).invert();
    }

    @Override
    public String toString() {
        return "NOT(" + this.getCondition().toString() + ")";
    }
}
