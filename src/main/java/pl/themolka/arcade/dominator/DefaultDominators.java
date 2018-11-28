package pl.themolka.arcade.dominator;

import java.util.Map;

public enum DefaultDominators implements Dominator<Object> {
    EVERYBODY(new Everybody()),
    EXCLUSIVE(new Exclusive()),
    LEAD(new Lead()),
    MAJORITY(new Majority()),
    NOBODY(new Nobody()),
    ;

    private final Dominator<Object> target;

    DefaultDominators(Dominator<Object> target) {
        this.target = target;
    }

    @Override
    public Map<Object, Integer> getDominators(Map<Object, Integer> input) {
        return this.target.getDominators(input);
    }

    public Dominator<Object> getDominator() {
        return this.target;
    }

    public static <T> Dominator<T> getDefault() {
        return (Dominator<T>) LEAD.target;
    }
}
