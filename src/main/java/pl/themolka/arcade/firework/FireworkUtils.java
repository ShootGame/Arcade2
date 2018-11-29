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

package pl.themolka.arcade.firework;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import pl.themolka.arcade.ArcadePlugin;

public final class FireworkUtils {
    private FireworkUtils() {
    }

    public static FireworkMeta createMeta(Firework firework, int power, FireworkEffect... effects) {
        return createMeta(firework.getFireworkMeta(), power, effects);
    }

    public static FireworkMeta createMeta(FireworkMeta meta, int power, FireworkEffect... effects) {
        meta.clearEffects();
        meta.addEffects(effects);
        meta.setPower(power);
        return meta;
    }

    public static Firework spawn(ArcadePlugin plugin, Location at, int power, FireworkEffect... effects) {
        return spawn(plugin, at, power, false, effects);
    }

    public static Firework spawn(ArcadePlugin plugin, Location at, int power, boolean denyDamage, FireworkEffect... effects) {
        Firework firework = at.getWorld().spawn(at, Firework.class);
        firework.setFireworkMeta(createMeta(firework, power, effects));
        DenyFireworkDamageService.apply(plugin, firework, denyDamage);
        return firework;
    }
}
