package pl.themolka.arcade.filter;

import org.bukkit.Locatable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class MaterialFilter extends AbstractFilter {
    public static final byte DATA_NULL = (byte) 0;

    private final List<MaterialData> container = new ArrayList<>();

    public MaterialFilter(List<MaterialData> container) {
        this.container.addAll(container);
    }

    @Override
    public FilterResult filter(Object object) {
        if (object instanceof Byte) {
            return FilterResult.of(this.matches((Byte) object));
        } else if (object instanceof ItemStack) {
            return FilterResult.of(this.matches((ItemStack) object));
        } else if (object instanceof Locatable) {
            return FilterResult.of(this.matches((Locatable) object));
        } else if (object instanceof Material) {
            return FilterResult.of(this.matches((Material) object));
        } else if (object instanceof MaterialData) {
            return FilterResult.of(this.matches((MaterialData) object));
        } else {
            return FilterResult.ABSTAIN;
        }
    }

    public boolean matches(byte data) {
        for (MaterialData value : this.getContainer()) {
            if (value.getData() == data) {
                return true;
            }
        }

        return false;
    }

    public boolean matches(ItemStack item) {
        return this.matches(item.getData());
    }

    public boolean matches(Locatable locatable) {
        return this.matches(locatable.getLocation().getBlock().getState().getData());
    }

    public boolean matches(Material material) {
        for (MaterialData value : this.getContainer()) {
            if (value.getItemType().equals(material)) {
                return true;
            }
        }

        return false;
    }

    public boolean matches(MaterialData data) {
        return this.matches(data.getItemType(), data.getData());
    }

    public boolean matches(Material material, byte data) {
        return this.matches(material) && this.matches(data);
    }

    public List<MaterialData> getContainer() {
        return this.container;
    }
}
