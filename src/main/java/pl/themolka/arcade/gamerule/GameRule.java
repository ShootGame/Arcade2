package pl.themolka.arcade.gamerule;

import java.util.Objects;

public class GameRule {
    private final GameRuleType type;
    private String value;

    public GameRule(GameRuleType type) {
        this(type, null);
    }

    public GameRule(GameRuleType type, String value) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.value = value;
    }

    public GameRuleType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
