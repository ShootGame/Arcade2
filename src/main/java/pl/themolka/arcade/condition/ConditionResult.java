package pl.themolka.arcade.condition;

public interface ConditionResult {
    boolean isTrue();

    default boolean isNotTrue() {
        return !this.isTrue();
    }

    boolean isFalse();

    default boolean isNotFalse() {
        return !this.isFalse();
    }

    default boolean toBoolean() {
        return this.isTrue();
    }
}
