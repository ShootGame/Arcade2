package pl.themolka.arcade.attribute;

import org.bukkit.attribute.AttributeModifier;

import java.util.Objects;

public class BoundedModifier {
    private final AttributeKey key;
    private final AttributeModifier modifier;

    public BoundedModifier(AttributeKey key, AttributeModifier modifier) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.modifier = Objects.requireNonNull(modifier, "modifier cannot be null");
    }

    public AttributeKey getKey() {
        return this.key;
    }

    public AttributeModifier getModifier() {
        return this.modifier;
    }
}
