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

package pl.themolka.arcade.util.material;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ColoredMaterials {
    private ColoredMaterials() {
    }

    public static final BiMap<Material, DyeColor> BANNERS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_BANNER, DyeColor.BLACK)
            .put(Material.BLUE_BANNER, DyeColor.BLUE)
            .put(Material.BROWN_BANNER, DyeColor.BROWN)
            .put(Material.CYAN_BANNER, DyeColor.CYAN)
            .put(Material.GRAY_BANNER, DyeColor.GRAY)
            .put(Material.GREEN_BANNER, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_BANNER, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_BANNER, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_BANNER, DyeColor.LIME)
            .put(Material.MAGENTA_BANNER, DyeColor.MAGENTA)
            .put(Material.ORANGE_BANNER, DyeColor.ORANGE)
            .put(Material.PINK_BANNER, DyeColor.PINK)
            .put(Material.PURPLE_BANNER, DyeColor.PURPLE)
            .put(Material.RED_BANNER, DyeColor.RED)
            .put(Material.WHITE_BANNER, DyeColor.WHITE)
            .put(Material.YELLOW_BANNER, DyeColor.YELLOW)
            .build();

    public static boolean isBanner(Material material) {
        return BANNERS.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> BEDS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_BED, DyeColor.BLACK)
            .put(Material.BLUE_BED, DyeColor.BLUE)
            .put(Material.BROWN_BED, DyeColor.BROWN)
            .put(Material.CYAN_BED, DyeColor.CYAN)
            .put(Material.GRAY_BED, DyeColor.GRAY)
            .put(Material.GREEN_BED, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_BED, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_BED, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_BED, DyeColor.LIME)
            .put(Material.MAGENTA_BED, DyeColor.MAGENTA)
            .put(Material.ORANGE_BED, DyeColor.ORANGE)
            .put(Material.PINK_BED, DyeColor.PINK)
            .put(Material.PURPLE_BED, DyeColor.PURPLE)
            .put(Material.RED_BED, DyeColor.RED)
            .put(Material.WHITE_BED, DyeColor.WHITE)
            .put(Material.YELLOW_BED, DyeColor.YELLOW)
            .build();

    public static boolean isBed(Material material) {
        return BEDS.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> CARPETS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_CARPET, DyeColor.BLACK)
            .put(Material.BLUE_CARPET, DyeColor.BLUE)
            .put(Material.BROWN_CARPET, DyeColor.BROWN)
            .put(Material.CYAN_CARPET, DyeColor.CYAN)
            .put(Material.GRAY_CARPET, DyeColor.GRAY)
            .put(Material.GREEN_CARPET, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_CARPET, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_CARPET, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_CARPET, DyeColor.LIME)
            .put(Material.MAGENTA_CARPET, DyeColor.MAGENTA)
            .put(Material.ORANGE_CARPET, DyeColor.ORANGE)
            .put(Material.PINK_CARPET, DyeColor.PINK)
            .put(Material.PURPLE_CARPET, DyeColor.PURPLE)
            .put(Material.RED_CARPET, DyeColor.RED)
            .put(Material.WHITE_CARPET, DyeColor.WHITE)
            .put(Material.YELLOW_CARPET, DyeColor.YELLOW)
            .build();

    public static boolean isCarpet(Material material) {
        return CARPETS.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> CONCRETE = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_CONCRETE, DyeColor.BLACK)
            .put(Material.BLUE_CONCRETE, DyeColor.BLUE)
            .put(Material.BROWN_CONCRETE, DyeColor.BROWN)
            .put(Material.CYAN_CONCRETE, DyeColor.CYAN)
            .put(Material.GRAY_CONCRETE, DyeColor.GRAY)
            .put(Material.GREEN_CONCRETE, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_CONCRETE, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_CONCRETE, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_CONCRETE, DyeColor.LIME)
            .put(Material.MAGENTA_CONCRETE, DyeColor.MAGENTA)
            .put(Material.ORANGE_CONCRETE, DyeColor.ORANGE)
            .put(Material.PINK_CONCRETE, DyeColor.PINK)
            .put(Material.PURPLE_CONCRETE, DyeColor.PURPLE)
            .put(Material.RED_CONCRETE, DyeColor.RED)
            .put(Material.WHITE_CONCRETE, DyeColor.WHITE)
            .put(Material.YELLOW_CONCRETE, DyeColor.YELLOW)
            .build();

    public static boolean isConcrete(Material material) {
        return CONCRETE.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> CONCRETE_POWDERS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_CONCRETE_POWDER, DyeColor.BLACK)
            .put(Material.BLUE_CONCRETE_POWDER, DyeColor.BLUE)
            .put(Material.BROWN_CONCRETE_POWDER, DyeColor.BROWN)
            .put(Material.CYAN_CONCRETE_POWDER, DyeColor.CYAN)
            .put(Material.GRAY_CONCRETE_POWDER, DyeColor.GRAY)
            .put(Material.GREEN_CONCRETE_POWDER, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_CONCRETE_POWDER, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_CONCRETE_POWDER, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_CONCRETE_POWDER, DyeColor.LIME)
            .put(Material.MAGENTA_CONCRETE_POWDER, DyeColor.MAGENTA)
            .put(Material.ORANGE_CONCRETE_POWDER, DyeColor.ORANGE)
            .put(Material.PINK_CONCRETE_POWDER, DyeColor.PINK)
            .put(Material.PURPLE_CONCRETE_POWDER, DyeColor.PURPLE)
            .put(Material.RED_CONCRETE_POWDER, DyeColor.RED)
            .put(Material.WHITE_CONCRETE_POWDER, DyeColor.WHITE)
            .put(Material.YELLOW_CONCRETE_POWDER, DyeColor.YELLOW)
            .build();

    public static boolean isConcretePowder(Material material) {
        return CONCRETE_POWDERS.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> GLAZED_TERRACOTTA = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_GLAZED_TERRACOTTA, DyeColor.BLACK)
            .put(Material.BLUE_GLAZED_TERRACOTTA, DyeColor.BLUE)
            .put(Material.BROWN_GLAZED_TERRACOTTA, DyeColor.BROWN)
            .put(Material.CYAN_GLAZED_TERRACOTTA, DyeColor.CYAN)
            .put(Material.GRAY_GLAZED_TERRACOTTA, DyeColor.GRAY)
            .put(Material.GREEN_GLAZED_TERRACOTTA, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_GLAZED_TERRACOTTA, DyeColor.LIME)
            .put(Material.MAGENTA_GLAZED_TERRACOTTA, DyeColor.MAGENTA)
            .put(Material.ORANGE_GLAZED_TERRACOTTA, DyeColor.ORANGE)
            .put(Material.PINK_GLAZED_TERRACOTTA, DyeColor.PINK)
            .put(Material.PURPLE_GLAZED_TERRACOTTA, DyeColor.PURPLE)
            .put(Material.RED_GLAZED_TERRACOTTA, DyeColor.RED)
            .put(Material.WHITE_GLAZED_TERRACOTTA, DyeColor.WHITE)
            .put(Material.YELLOW_GLAZED_TERRACOTTA, DyeColor.YELLOW)
            .build();

    public static boolean isGlazedTerracotta(Material material) {
        return GLAZED_TERRACOTTA.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> SHULKER_BOXES = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_SHULKER_BOX, DyeColor.BLACK)
            .put(Material.BLUE_SHULKER_BOX, DyeColor.BLUE)
            .put(Material.BROWN_SHULKER_BOX, DyeColor.BROWN)
            .put(Material.CYAN_SHULKER_BOX, DyeColor.CYAN)
            .put(Material.GRAY_SHULKER_BOX, DyeColor.GRAY)
            .put(Material.GREEN_SHULKER_BOX, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_SHULKER_BOX, DyeColor.LIME)
            .put(Material.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA)
            .put(Material.ORANGE_SHULKER_BOX, DyeColor.ORANGE)
            .put(Material.PINK_SHULKER_BOX, DyeColor.PINK)
            .put(Material.PURPLE_SHULKER_BOX, DyeColor.PURPLE)
            .put(Material.RED_SHULKER_BOX, DyeColor.RED)
            .put(Material.WHITE_SHULKER_BOX, DyeColor.WHITE)
            .put(Material.YELLOW_SHULKER_BOX, DyeColor.YELLOW)
            .build();

    public static boolean isShulkerBox(Material material) {
        return SHULKER_BOXES.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> STAINED_GLASS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_STAINED_GLASS, DyeColor.BLACK)
            .put(Material.BLUE_STAINED_GLASS, DyeColor.BLUE)
            .put(Material.BROWN_STAINED_GLASS, DyeColor.BROWN)
            .put(Material.CYAN_STAINED_GLASS, DyeColor.CYAN)
            .put(Material.GRAY_STAINED_GLASS, DyeColor.GRAY)
            .put(Material.GREEN_STAINED_GLASS, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_STAINED_GLASS, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_STAINED_GLASS, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_STAINED_GLASS, DyeColor.LIME)
            .put(Material.MAGENTA_STAINED_GLASS, DyeColor.MAGENTA)
            .put(Material.ORANGE_STAINED_GLASS, DyeColor.ORANGE)
            .put(Material.PINK_STAINED_GLASS, DyeColor.PINK)
            .put(Material.PURPLE_STAINED_GLASS, DyeColor.PURPLE)
            .put(Material.RED_STAINED_GLASS, DyeColor.RED)
            .put(Material.WHITE_STAINED_GLASS, DyeColor.WHITE)
            .put(Material.YELLOW_STAINED_GLASS, DyeColor.YELLOW)
            .build();

    public static boolean isStainedGlass(Material material) {
        return STAINED_GLASS.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> STAINED_GLASS_PANES = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_STAINED_GLASS_PANE, DyeColor.BLACK)
            .put(Material.BLUE_STAINED_GLASS_PANE, DyeColor.BLUE)
            .put(Material.BROWN_STAINED_GLASS_PANE, DyeColor.BROWN)
            .put(Material.CYAN_STAINED_GLASS_PANE, DyeColor.CYAN)
            .put(Material.GRAY_STAINED_GLASS_PANE, DyeColor.GRAY)
            .put(Material.GREEN_STAINED_GLASS_PANE, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_STAINED_GLASS_PANE, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_STAINED_GLASS_PANE, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_STAINED_GLASS_PANE, DyeColor.LIME)
            .put(Material.MAGENTA_STAINED_GLASS_PANE, DyeColor.MAGENTA)
            .put(Material.ORANGE_STAINED_GLASS_PANE, DyeColor.ORANGE)
            .put(Material.PINK_STAINED_GLASS_PANE, DyeColor.PINK)
            .put(Material.PURPLE_STAINED_GLASS_PANE, DyeColor.PURPLE)
            .put(Material.RED_STAINED_GLASS_PANE, DyeColor.RED)
            .put(Material.WHITE_STAINED_GLASS_PANE, DyeColor.WHITE)
            .put(Material.YELLOW_STAINED_GLASS_PANE, DyeColor.YELLOW)
            .build();

    public static boolean isStainedGlassPane(Material material) {
        return STAINED_GLASS_PANES.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> TERRACOTTA = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_TERRACOTTA, DyeColor.BLACK)
            .put(Material.BLUE_TERRACOTTA, DyeColor.BLUE)
            .put(Material.BROWN_TERRACOTTA, DyeColor.BROWN)
            .put(Material.CYAN_TERRACOTTA, DyeColor.CYAN)
            .put(Material.GRAY_TERRACOTTA, DyeColor.GRAY)
            .put(Material.GREEN_TERRACOTTA, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_TERRACOTTA, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_TERRACOTTA, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_TERRACOTTA, DyeColor.LIME)
            .put(Material.MAGENTA_TERRACOTTA, DyeColor.MAGENTA)
            .put(Material.ORANGE_TERRACOTTA, DyeColor.ORANGE)
            .put(Material.PINK_TERRACOTTA, DyeColor.PINK)
            .put(Material.PURPLE_TERRACOTTA, DyeColor.PURPLE)
            .put(Material.RED_TERRACOTTA, DyeColor.RED)
            .put(Material.WHITE_TERRACOTTA, DyeColor.WHITE)
            .put(Material.YELLOW_TERRACOTTA, DyeColor.YELLOW)
            .build();

    public static boolean isTerracotta(Material material) {
        return TERRACOTTA.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> WALL_BANNERS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_WALL_BANNER, DyeColor.BLACK)
            .put(Material.BLUE_WALL_BANNER, DyeColor.BLUE)
            .put(Material.BROWN_WALL_BANNER, DyeColor.BROWN)
            .put(Material.CYAN_WALL_BANNER, DyeColor.CYAN)
            .put(Material.GRAY_WALL_BANNER, DyeColor.GRAY)
            .put(Material.GREEN_WALL_BANNER, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_WALL_BANNER, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_WALL_BANNER, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_WALL_BANNER, DyeColor.LIME)
            .put(Material.MAGENTA_WALL_BANNER, DyeColor.MAGENTA)
            .put(Material.ORANGE_WALL_BANNER, DyeColor.ORANGE)
            .put(Material.PINK_WALL_BANNER, DyeColor.PINK)
            .put(Material.PURPLE_WALL_BANNER, DyeColor.PURPLE)
            .put(Material.RED_WALL_BANNER, DyeColor.RED)
            .put(Material.WHITE_WALL_BANNER, DyeColor.WHITE)
            .put(Material.YELLOW_WALL_BANNER, DyeColor.YELLOW)
            .build();

    public static boolean isWallBanner(Material material) {
        return WALL_BANNERS.containsKey(requireMaterial(material));
    }

    public static final BiMap<Material, DyeColor> WOOLS = ImmutableBiMap.<Material, DyeColor>builder()
            .put(Material.BLACK_WOOL, DyeColor.BLACK)
            .put(Material.BLUE_WOOL, DyeColor.BLUE)
            .put(Material.BROWN_WOOL, DyeColor.BROWN)
            .put(Material.CYAN_WOOL, DyeColor.CYAN)
            .put(Material.GRAY_WOOL, DyeColor.GRAY)
            .put(Material.GREEN_WOOL, DyeColor.GREEN)
            .put(Material.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE)
            .put(Material.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY)
            .put(Material.LIME_WOOL, DyeColor.LIME)
            .put(Material.MAGENTA_WOOL, DyeColor.MAGENTA)
            .put(Material.ORANGE_WOOL, DyeColor.ORANGE)
            .put(Material.PINK_WOOL, DyeColor.PINK)
            .put(Material.PURPLE_WOOL, DyeColor.PURPLE)
            .put(Material.RED_WOOL, DyeColor.RED)
            .put(Material.WHITE_WOOL, DyeColor.WHITE)
            .put(Material.YELLOW_WOOL, DyeColor.YELLOW)
            .build();

    public static boolean isWool(Material material) {
        return WOOLS.containsKey(requireMaterial(material));
    }

    public static final Map<Material, DyeColor> COLORS;

    public static boolean isColored(Material material) {
        return COLORS.containsKey(requireMaterial(material));
    }

    public static final Map<Material, BiMap<DyeColor, Material>> TYPES;

    static {
        List<BiMap<Material, DyeColor>> maps = new ArrayList<>(12 * 16);
        maps.add(BANNERS);
        maps.add(BEDS);
        maps.add(CARPETS);
        maps.add(CONCRETE);
        maps.add(CONCRETE_POWDERS);
        maps.add(GLAZED_TERRACOTTA);
        maps.add(SHULKER_BOXES);
        maps.add(STAINED_GLASS);
        maps.add(STAINED_GLASS_PANES);
        maps.add(TERRACOTTA);
        maps.add(WALL_BANNERS);
        maps.add(WOOLS);

        // colors
        BiMap<Material, DyeColor> colors = HashBiMap.create();
        Map<Material, BiMap<DyeColor, Material>> types = new HashMap<>();

        for (BiMap<Material, DyeColor> map : maps) {
            colors.putAll(map);

            for (Material material : map.keySet()) {
                types.put(material, map.inverse());
            }
        }

        COLORS = ImmutableMap.copyOf(colors);
        TYPES = ImmutableMap.copyOf(types);
    }

    private static Material requireMaterial(Material material) {
        return Objects.requireNonNull(material, "material cannot be null");
    }
}
