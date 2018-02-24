package pl.themolka.arcade.filter.matcher;

import org.bukkit.Locatable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;

import java.util.Objects;

@MatcherId("material")
public class MaterialMatcher extends Matcher {
    public static final MatcherParser PARSER = new MaterialParser();

    public static final byte DATA_NULL = 0;

    private final MaterialData material;

    public MaterialMatcher(Material material) {
        this(new MaterialData(material));
    }

    public MaterialMatcher(MaterialData material) {
        this.material = material;
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
        return Objects.equals(this.material.getData(), data);
    }

    public boolean matches(ItemStack item) {
        return this.matches(item.getData());
    }

    public boolean matches(Locatable locatable) {
        return this.matches(locatable.getLocation().getBlock().getState().getData());
    }

    public boolean matches(Material material) {
        return Objects.equals(this.material.getItemType(), material);
    }

    public boolean matches(MaterialData data) {
        return Objects.equals(this.material, data);
    }

    public boolean matches(Material material, byte data) {
        return this.matches(material) && this.matches(data);
    }
}

class MaterialParser implements MatcherParser<MaterialMatcher> {
    @Override
    public MaterialMatcher parsePrimitive(Node node, ParserContext context) throws ParserException {
        return new MaterialMatcher(context.type(MaterialData.class).parse(node).orFail());
    }
}
