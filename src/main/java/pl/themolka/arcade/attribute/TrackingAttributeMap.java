package pl.themolka.arcade.attribute;

import net.minecraft.server.AttributeMapBase;

import java.util.LinkedHashSet;
import java.util.Set;

public class TrackingAttributeMap extends AttributeMap {
    private final Set<AttributeKey> tracking = new LinkedHashSet<>();

    public TrackingAttributeMap(AttributeMapBase mojang) {
        super(mojang);
    }

    @Override
    public Attribute getAttribute(AttributeKey key) {
        Attribute attribute = super.getAttribute(key);
        if (attribute != null) {
            this.subscribe(key);
        }

        return attribute;
    }

    public Set<AttributeKey> getTracking() {
        return new LinkedHashSet<>(this.tracking);
    }

    public void subscribe(AttributeKey key) {
        this.tracking.add(key);
    }

    public void unsubscribe(AttributeKey key) {
        this.tracking.remove(key);
    }

    public void unsubscribeAll() {
        this.tracking.clear();
    }
}
