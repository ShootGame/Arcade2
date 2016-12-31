package pl.themolka.arcade.gamerule;

public class GameRule {
    private final GameRuleType type;
    private String value;

    public GameRule(GameRuleType type) {
        this(type, null);
    }

    public GameRule(GameRuleType type, String value) {
        this.type = type;
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
