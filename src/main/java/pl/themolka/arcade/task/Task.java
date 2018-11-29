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

public class Task extends SimpleTaskListener {
    public static final int DEFAULT_TASK_ID = -1;

    private int taskId = DEFAULT_TASK_ID;
    private final TaskManager tasks;

    public Task(TaskManager tasks) {
        this.tasks = tasks;
    }

    public boolean cancelTask() {
        return this.tasks.cancel(this);
    }

    public int getTaskId() {
        return this.taskId;
    }

    public TaskManager getTasks() {
        return this.tasks;
    }

    public boolean isTaskRunning() {
        return this.taskId != DEFAULT_TASK_ID;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Task scheduleAsyncTask() {
        this.taskId = this.tasks.scheduleAsync(this);
        return this;
    }

    public Task scheduleSyncTask() {
        this.taskId = this.tasks.scheduleSync(this);
        return this;
    }
}
