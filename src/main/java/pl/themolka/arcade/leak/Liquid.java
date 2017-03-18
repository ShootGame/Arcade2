package pl.themolka.arcade.leak;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Liquid {
    public static final Type DEFAULT_TYPE = Type.LAVA;

    private final List<Block> snapshot = new ArrayList<>();
    private Type type;

    public boolean addSnapshot(Block block) {
        return block.isLiquid() && this.snapshot.add(block);
    }

    public boolean addSnapshot(List<Block> blocks) {
        int index = 0;
        for (Block block : blocks) {
            if (this.addSnapshot(block)) {
                index++;
            }
        }

        return index == blocks.size();
    }

    public void clearSnapshot() {
        this.snapshot.clear();
    }

    public List<Block> createSnapshot(List<Block> of) {
        List<Block> results = new ArrayList<>();
        for (Block block : of) {
            if (this.getType().accepts(block.getType())) {
                results.add(block);
            }
        }

        return results;
    }

    public Type findType(List<Block> blocks) {
        for (Block block : blocks) {
            Type type = Type.find(block.getType());
            if (type != null) {
                return type;
            }
        }

        return null;
    }

    public List<Block> getSnapshot() {
        return this.snapshot;
    }

    public Type getType() {
        return this.type;
    }

    public boolean isSnapshot(Block block) {
        return this.snapshot.contains(block);
    }

    public boolean removeSnapshot(Block block) {
        return this.snapshot.remove(block);
    }

    public int replaceLiquid(Type newType) {
        if (this.getType().equals(newType)) {
            return 0;
        }

        int index = 0;
        for (Block block : this.getSnapshot()) {
            for (Material liquid : this.getType().getMaterials()) {
                if (block.getType().equals(liquid)) {
                    block.setType(newType.getLiquid());
                    index++;
                    break;
                }
            }
        }

        this.setType(newType);

        return index;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(Material material) {
        this.setType(Type.find(material));
    }

    public enum Type {
        LAVA(Material.LAVA, Material.STATIONARY_LAVA),
        WATER(Material.WATER, Material.STATIONARY_WATER),
        ;

        private final Material[] materials;

        Type(Material... materials) {
            this.materials = materials;
        }

        public boolean accepts(Material material) {
            for (Material type : this.getMaterials()) {
                if (type.equals(material)) {
                    return true;
                }
            }

            return false;
        }

        public Material getLiquid() {
            return this.getMaterials()[0];
        }

        public Material[] getMaterials() {
            return this.materials;
        }

        public static Type find(Material material) {
            for (Type type : values()) {
                if (type.accepts(material)) {
                    return type;
                }
            }

            return null;
        }
    }
}
