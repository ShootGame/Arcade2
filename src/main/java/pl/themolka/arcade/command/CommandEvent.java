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

package pl.themolka.arcade.command;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameEvent;

public class CommandEvent extends GameEvent {
    private final Sender sender;
    private final CommandContext context;

    public CommandEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
        super(plugin);

        this.sender = sender;
        this.context = context;
    }

    public Sender getSender() {
        return this.sender;
    }

    public CommandContext getContext() {
        return this.context;
    }
}
