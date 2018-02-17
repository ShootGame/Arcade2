package pl.themolka.arcade.condition;

import java.util.Collection;

public class AllCondition extends MultiCondition {
    public AllCondition(Condition... all) {
        super(all);
    }

    public AllCondition(Collection<Condition> all) {
        super(all);
    }

    @Override
    public boolean defaultValue() {
        return true;
    }

    @Override
    public Result test(Condition condition) {
        return condition.test() ? Result.ABSTAIN : Result.FALSE;
    }
}
