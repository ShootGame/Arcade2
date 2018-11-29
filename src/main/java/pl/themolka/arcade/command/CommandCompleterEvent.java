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

import java.util.ArrayList;
import java.util.List;

public class CommandCompleterEvent extends CommandEvent {
    private final List<String> results = new ArrayList<>();

    public CommandCompleterEvent(ArcadePlugin plugin, Sender sender, CommandContext context) {
        this(plugin, sender, context, null);
    }

    public CommandCompleterEvent(ArcadePlugin plugin, Sender sender, CommandContext context, List<String> results) {
        super(plugin, sender, context);

        if (results != null) {
            this.results.addAll(results);
        }
    }

    public boolean addResult(String result) {
        return this.results.add(result);
    }

    public List<String> getResults() {
        return this.results;
    }
}
