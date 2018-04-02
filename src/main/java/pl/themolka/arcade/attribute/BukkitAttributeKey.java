package pl.themolka.arcade.attribute;

import org.bukkit.attribute.Attribute;

import java.util.Objects;

public class BukkitAttributeKey extends AttributeKey {
    private final Attribute bukkit;

    public BukkitAttributeKey(Attribute bukkit) {
        this.bukkit = Objects.requireNonNull(bukkit, "bukkit cannot be null");
    }

    @Override
    public String key() {
        return this.bukkit.getName();
    }

    public Attribute getBukkit() {
        return this.bukkit;
    }
}
