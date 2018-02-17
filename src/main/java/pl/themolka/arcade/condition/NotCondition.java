package pl.themolka.arcade.condition;

public class NotCondition extends SingleCondition {
    public NotCondition(Condition condition) {
        super(condition);
    }

    @Override
    public boolean test(Condition condition) {
        return !condition.test();
    }
}
