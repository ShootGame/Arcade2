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

package pl.themolka.arcade.objective.wool;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.goal.GoalFireworkHandler;

public class WoolPlaceFireworks extends GoalFireworkHandler {
    public WoolPlaceFireworks(boolean enabled) {
        super(enabled);
    }

    public WoolPlaceFireworks(Ref<Boolean> enabled) {
        super(enabled);
    }

    @Handler(priority = Priority.LAST)
    public void onWoolPlace(WoolPlaceEvent event) {
        if (this.isEnabled() && !event.isCanceled()) {
            this.fireComplete(event.getPlugin(),
                              event.getCompleter().getBukkit().getLocation(),
                              event.getGoal().getColor().getFireworkColor());
        }
    }
}
