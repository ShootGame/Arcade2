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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.service.Service;
import pl.themolka.arcade.service.ServiceId;

import java.util.Objects;

@ServiceId("DenyFireworkDamageService")
public class DenyFireworkDamageService extends Service {
    public static final String METADATA_KEY = "denyFireworkDamage";

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void denyDamageFromFireworks(EntityDamageByEntityEvent event) {
        Plugin plugin = this.getPlugin();
        Entity damager = event.getDamager();

        if (damager instanceof Firework) {
            MetadataValue metadata = damager.getMetadata(METADATA_KEY, plugin);

            if (metadata instanceof DamageMetadata && ((DamageMetadata) metadata).value()) {
                damager.removeMetadata(METADATA_KEY, plugin);
                event.setCancelled(true);
            }
        }
    }

    static class DamageMetadata extends FixedMetadataValue {
        DamageMetadata(Plugin owningPlugin, boolean denyDamage) {
            super(owningPlugin, denyDamage);
        }

        @Override
        public Boolean value() {
            return (boolean) super.value();
        }
    }

    //
    // Applying Metadata to Fireworks
    //

    public static Firework apply(GameModule module, Firework firework, boolean denyDamage) {
        return apply(Objects.requireNonNull(module, "module cannot be null").getModule(), firework, denyDamage);
    }

    public static Firework apply(Module<?> module, Firework firework, boolean denyDamage) {
        return apply(Objects.requireNonNull(module, "module cannot be null").getGame(), firework, denyDamage);
    }

    public static Firework apply(Game game, Firework firework, boolean denyDamage) {
        return apply(Objects.requireNonNull(game, "game cannot be null").getPlugin(), firework, denyDamage);
    }

    public static Firework apply(ArcadePlugin plugin, Firework firework, boolean denyDamage) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        Objects.requireNonNull(firework, "firework cannot be null");
        firework.setMetadata(METADATA_KEY, new DamageMetadata(plugin, denyDamage));
        return firework;
    }
}
