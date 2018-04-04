package pl.themolka.arcade.condition;

public abstract class SingleCondition<K, V extends ConditionResult> implements Condition<K, V> {
    private final Condition<K, V> condition;

    public SingleCondition(Condition<K, V> condition) {
        this.condition = condition;
    }

    @Override
    public V query(K k) {
        return this.query(k, this.condition);
    }

    public Condition<K, V> getCondition() {
        return this.condition;
    }

    public abstract V query(K k, Condition<K, V> condition);
}
