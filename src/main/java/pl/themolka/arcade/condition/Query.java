package pl.themolka.arcade.condition;

import java.util.Collection;

public final class Query {
    private Query() {
    }

    public static <T> AndCondition<T> and(Collection<Condition<T, AbstainableResult>> all) {
        return new AndCondition<>(all);
    }

    public static <T> OrCondition<T> or(Collection<Condition<T, AbstainableResult>> any) {
        return new OrCondition<>(any);
    }

    public static <T, V extends InvertableResult<V>> NandCondition<T, V> nand(Collection<Condition<T, V>> nand) {
        return new NandCondition<>(nand);
    }

    public static <T> NoneCondition<T> none(Collection<Condition<T, AbstainableResult>> none) {
        return new NoneCondition<>(none);
    }

    public static <T, V extends InvertableResult<V>> NorCondition<T, V> nor(Collection<Condition<T, V>> nor) {
        return new NorCondition<>(nor);
    }

    public static <T, V extends InvertableResult<V>> NotCondition<T, V> not(Condition<T, V> not) {
        return new NotCondition<>(not);
    }

    public static <T, V extends InvertableResult<V>> XnorCondition<T, V> xnor(Collection<Condition<T, V>> xnor) {
        return new XnorCondition<>(xnor);
    }

    public static <T> XorCondition<T> xor(Collection<Condition<T, AbstainableResult>> xor) {
        return new XorCondition<>(xor);
    }
}
