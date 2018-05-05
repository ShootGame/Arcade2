package pl.themolka.arcade.objective.core;

import org.bukkit.Material;
import org.bukkit.block.Block;

public enum Liquid {
    LAVA(Material.LAVA, Material.STATIONARY_LAVA),
    WATER(Material.WATER, Material.STATIONARY_WATER),
    ;

    private final Material[] materials;

    Liquid(Material... materials) {
        this.materials = materials;
    }

    public boolean accepts(Block block) {
        return this.accepts(block.getType());
    }

    public boolean accepts(Material material) {
        for (Material type : this.materials) {
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

    public static Liquid find(Material material) {
        if (material != null) {
            for (Liquid liquid : values()) {
                if (liquid.accepts(material)) {
                    return liquid;
                }
            }
        }

        return null;
    }
}
