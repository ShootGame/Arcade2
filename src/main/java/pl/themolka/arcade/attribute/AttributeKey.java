package pl.themolka.arcade.attribute;

import org.bukkit.attribute.Attribute;

import java.util.Objects;

public abstract class AttributeKey {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AttributeKey && this.key().equals(((AttributeKey) obj).key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key());
    }

    public abstract String key();

    @Override
    public String toString() {
        return this.key();
    }

    //
    // Instancing
    //

    public static BukkitAttributeKey bukkit(Attribute attribute) {
        return new BukkitAttributeKey(attribute);
    }

    public static FixedAttributeKey fixed(String key) {
        return fixed(FixedAttributeKey.DEFAULT_NAMESPACE, key);
    }

    public static FixedAttributeKey fixed(String namespace, String key) {
        return new FixedAttributeKey(namespace, key);
    }
}
