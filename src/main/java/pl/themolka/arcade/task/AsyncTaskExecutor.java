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

public class AsyncTaskExecutor extends TaskExecutor {
    private final AsyncTaskThread thread;

    public AsyncTaskExecutor(ArcadePlugin plugin, int id, TaskListener listener) {
        super(plugin, id, listener);

        this.thread = new AsyncTaskThread(plugin, this);
    }

    @Override
    public void createTask() {
        super.createTask();
        this.getThread().start();
    }

    @Override
    public void destroyTask() {
        this.getThread().interrupt();
        super.destroyTask();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public AsyncTaskThread getThread() {
        return this.thread;
    }
}
