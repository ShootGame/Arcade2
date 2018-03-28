package pl.themolka.arcade.attribute;

import org.bukkit.attribute.ItemAttributeModifier;

import java.util.Objects;

public class BoundedItemModifier extends BoundedModifier {
    private final ItemAttributeModifier itemModifier;

    public BoundedItemModifier(AttributeKey key, ItemAttributeModifier itemModifier) {
        super(key, Objects.requireNonNull(itemModifier, "itemModifier cannot be null").getModifier());
        this.itemModifier = itemModifier;
    }

    public ItemAttributeModifier getItemModifier() {
        return this.itemModifier;
    }
}
