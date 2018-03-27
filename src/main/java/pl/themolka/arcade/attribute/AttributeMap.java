package pl.themolka.arcade.attribute;

import net.minecraft.server.AttributeInstance;
import net.minecraft.server.AttributeMapBase;

import java.util.Objects;

public class AttributeMap implements Attributable {
    private final AttributeMapBase mojang;

    public AttributeMap(AttributeMapBase mojang) {
        this.mojang = Objects.requireNonNull(mojang, "mojang cannot be null");
    }

    @Override
    public Attribute getAttribute(AttributeKey key) {
        Objects.requireNonNull(key, "key cannot be null");
        AttributeInstance nms = this.mojang.a(key.key());
        return nms != null ? new Attribute(nms, key) : null;
    }
}
