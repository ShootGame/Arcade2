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

package pl.themolka.arcade.service;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.task.SimpleTaskListener;

@ServiceId("SetNextRestart")
public class SetNextRestartService extends Service {
    @Override
    protected void loadService() {
        this.getPlugin().getTasks().scheduleAsync(new SimpleTaskListener() {
            boolean done = false;

            @Override
            public void onDay(long days) {
                if (!this.done) {
                    ArcadePlugin plugin = getPlugin();
                    plugin.getLogger().info("Server ran now for 24 hours. Will be restarting soon...");
                    plugin.getGames().setNextRestart(true);
                    this.done = true;
                }
            }
        });
    }
}
