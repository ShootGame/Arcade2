package pl.themolka.arcade.condition;

public interface AbstainableResult extends ConditionResult {
    boolean isAbstaining();

    default boolean isNotAbstaining() {
        return !this.isAbstaining();
    }
}
