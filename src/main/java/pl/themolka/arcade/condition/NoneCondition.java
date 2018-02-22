package pl.themolka.arcade.condition;

import java.util.Collection;

public class NoneCondition extends AnyCondition {
    public NoneCondition(Condition... none) {
        super(none);
    }

    public NoneCondition(Collection<Condition> none) {
        super(none);
    }

    @Override
    public boolean defaultValue() {
        return true;
    }

    @Override
    public Result test(Condition condition) {
        return condition.test() ? Result.FALSE : Result.ABSTAIN;
    }
}
