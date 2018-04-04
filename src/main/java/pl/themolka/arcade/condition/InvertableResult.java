package pl.themolka.arcade.condition;

public interface InvertableResult<V extends ConditionResult> extends ConditionResult {
    V invert();
}
