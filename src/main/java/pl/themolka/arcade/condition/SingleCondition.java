package pl.themolka.arcade.condition;

public abstract class SingleCondition<T> implements Condition<T> {
    private final Condition<T> condition;

    public SingleCondition(Condition<T> condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(T t) {
        return this.test(t, this.condition);
    }

    public Condition<T> getCondition() {
        return this.condition;
    }

    public abstract boolean test(T t, Condition<T> condition);
}
