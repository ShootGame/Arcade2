package pl.themolka.arcade.gamerule;

import org.bukkit.World;
import pl.themolka.arcade.util.Applicable;

import java.util.Objects;

public class GameRule implements Applicable<World> {
    private final String key;
    private Object value;

    public GameRule(String key) {
        this(key, null);
    }

    public GameRule(String key, Object value) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.value = value;
    }

    @Override
    public void apply(World world) {
        world.setGameRuleValue(this.key, this.getValueString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameRule) {
            GameRule that = (GameRule) obj;
            return  Objects.equals(this.key, that.key) &&
                    Objects.equals(this.value, that.value);
        }

        return false;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    public String getValueString() {
        return Objects.toString(this.value, "");
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    public boolean isApplicable() {
        return this.value != null;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
