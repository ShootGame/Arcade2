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

package pl.themolka.arcade.event;

import net.engio.mbassy.bus.MBassador;
import pl.themolka.arcade.ArcadePlugin;

public class EventBus extends MBassador<Event> {
    public EventBus(ArcadePlugin plugin) {
        super(new PublicationErrorHandler(plugin.getLogger()));
    }

    public <E extends Event> E postEvent(E event) {
        this.publish(event);
        return event;
    }
}
