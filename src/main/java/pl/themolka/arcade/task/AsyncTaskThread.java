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

package pl.themolka.arcade.task;

import pl.themolka.arcade.ArcadePlugin;

import java.time.Instant;
import java.util.logging.Level;

public class AsyncTaskThread extends Thread {
    private final ArcadePlugin plugin;

    private final AsyncTaskExecutor executor;

    public AsyncTaskThread(ArcadePlugin plugin, AsyncTaskExecutor executor) {
        super("Async Task #" + executor.getId());
        this.plugin = plugin;

        this.executor = executor;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            Instant now = Instant.now();

            try {
                this.getExecutor().run();
            } catch (Throwable th) {
                if (this.getId() != Task.DEFAULT_TASK_ID && !this.isInterrupted()) {
                    this.plugin.getLogger().log(Level.SEVERE, "Could not run async task executor #" + this.getExecutor().getId(), th);
                }
            }

            try {
                long sleep = Instant.now().toEpochMilli() - now.toEpochMilli();
                Thread.sleep(Math.max(1L, TaskExecutor.TICK_SLEEP - sleep));
            } catch (InterruptedException ignored) {
            }
        }
    }

    public AsyncTaskExecutor getExecutor() {
        return this.executor;
    }
}
