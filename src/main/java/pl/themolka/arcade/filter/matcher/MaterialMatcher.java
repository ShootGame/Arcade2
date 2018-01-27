package pl.themolka.arcade.filter.matcher;

import org.bukkit.Locatable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.filter.FilterResult;

import java.util.ArrayList;
import java.util.List;

@MatcherId("material")
public class MaterialMatcher extends Matcher {
    public static final byte DATA_NULL = (byte) 0;

    private final List<MaterialData> container = new ArrayList<>();

    public MaterialMatcher(List<MaterialData> container) {
        this.container.addAll(container);
    }

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof Block) {
            return this.of(this.matches((Block) object));
        } else if (object instanceof BlockState) {
            return this.of(this.matches((BlockState) object));
        } else if (object instanceof Byte) {
            return this.of(this.matches((byte) object));
        } else if (object instanceof ItemStack) {
            return this.of(this.matches((ItemStack) object));
        } else if (object instanceof Locatable) {
            return this.of(this.matches((Locatable) object));
        } else if (object instanceof Material) {
            return this.of(this.matches((Material) object));
        } else if (object instanceof MaterialData) {
            return this.of(this.matches((MaterialData) object));
        }

        return this.abstain();
    }

    public boolean matches(Block block) {
        return this.matches(block.getType(), block.getData());
    }

    public boolean matches(BlockState blockState) {
        return this.matches(blockState.getMaterialData());
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
