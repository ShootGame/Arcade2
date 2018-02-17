package pl.themolka.arcade.condition;

import java.util.Collection;

public final class Conditions {
    private Conditions() {
    }

    public static AllCondition all(Condition... all) {
        return new AllCondition(all);
    }

    public static AllCondition all(Collection<Condition> all) {
        return new AllCondition(all);
    }

    public static AnyCondition any(Condition... any) {
        return new AnyCondition(any);
    }

    public static AnyCondition any(Collection<Condition> any) {
        return new AnyCondition(any);
    }

    public static NoneCondition none(Condition... none) {
        return new NoneCondition(none);
    }

    public static NoneCondition none(Collection<Condition> none) {
        return new NoneCondition(none);
    }

    public static NotCondition not(Condition not) {
        return new NotCondition(not);
    }
}
