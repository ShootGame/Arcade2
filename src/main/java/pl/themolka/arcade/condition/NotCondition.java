package pl.themolka.arcade.condition;

public class NotCondition<T> extends SingleCondition<T> {
    public NotCondition(Condition<T> condition) {
        super(condition);
    }

    @Override
    public boolean test(T t, Condition<T> condition) {
        return !condition.test(t);
    }
}
