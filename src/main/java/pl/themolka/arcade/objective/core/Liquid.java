/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
