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

import net.engio.mbassy.listener.Handler;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.GoalFireworkHandler;
import pl.themolka.arcade.util.Color;

public class CoreLeakFireworks extends GoalFireworkHandler {
    // We are unable to fire fireworks based on participator's color.
    public static final Color FIREWORK_COLOR = Color.WHITE;

    public CoreLeakFireworks(boolean enabled) {
        super(enabled);
    }

    public CoreLeakFireworks(Ref<Boolean> enabled) {
        super(enabled);
    }

    @Handler(priority = Priority.LAST)
    public void onCoreLeak(CoreLeakEvent event) {
        if (this.isEnabled() && !event.isCanceled()) {
            Plugin plugin = event.getPlugin();

            for (Location at : this.getRegionCorners(event.getGoal().getRegion().getBounds())) {
                this.fireComplete(plugin, at, FIREWORK_COLOR);
            }
        }
    }
}
