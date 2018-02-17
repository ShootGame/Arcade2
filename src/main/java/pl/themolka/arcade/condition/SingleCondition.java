package pl.themolka.arcade.condition;

public abstract class SingleCondition implements Condition {
    private final Condition condition;

    public SingleCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean test() {
        return this.test(this.condition);
    }

    public Condition getCondition() {
        return this.condition;
    }

    public abstract boolean test(Condition condition);
}
