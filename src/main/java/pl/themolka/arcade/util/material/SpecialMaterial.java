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

import org.bukkit.Material;

import java.util.Objects;
import java.util.function.Predicate;

public class SpecialMaterial implements Predicate<Material> {
    private final Material original;

    public SpecialMaterial(Material original) {
        this.original = Objects.requireNonNull(original, "original");
    }

    @Override
    public boolean test(Material material) {
        return Objects.equals(this.original, material);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialMaterial that = (SpecialMaterial) o;
        return original == that.original;
    }

    @Override
    public int hashCode() {
        return Objects.hash(original);
    }

    public Material getOriginal() {
        return this.original;
    }

    @Override
    public String toString() {
        return this.original.toString();
    }
}
