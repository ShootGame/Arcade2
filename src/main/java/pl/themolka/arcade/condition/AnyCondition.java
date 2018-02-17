package pl.themolka.arcade.condition;

import java.util.Collection;

public class AnyCondition extends MultiCondition {
    public AnyCondition(Condition... any) {
        super(any);
    }

    public AnyCondition(Collection<Condition> any) {
        super(any);
    }

    @Override
    public boolean defaultValue() {
        return false;
    }

    @Override
    public Result test(Condition condition) {
        return condition.test() ? Result.TRUE : Result.ABSTAIN;
    }
}
