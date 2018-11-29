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

import java.util.List;

public interface TaskManager {
    default boolean cancel(int taskId) {
        return taskId != Task.DEFAULT_TASK_ID && this.cancel(this.getTask(taskId));
    }

    default boolean cancel(Task task) {
        return this.cancel(task.getTaskId());
    }

    boolean cancel(TaskExecutor task);

    default int cancelAll() {
        int result = 0;
        for (TaskExecutor task : this.getTasks()) {
            if (this.cancel(task)) {
                result++;
            }
        }

        return result;
    }

    default TaskExecutor getTask(int taskId) {
        for (TaskExecutor task : this.getTasks()) {
            if (task.getId() == taskId) {
                return task;
            }
        }

        return null;
    }

    List<TaskExecutor> getTasks();

    int schedule(TaskExecutor executor);

    default int scheduleAsync(Runnable task) {
        return this.scheduleAsync(new TickTask(task));
    }

    int scheduleAsync(TaskListener task);

    default int scheduleSync(Runnable task) {
        return this.scheduleSync(new TickTask(task));
    }

    int scheduleSync(TaskListener task);

    class TickTask extends SimpleTaskListener {
        private final Runnable task;

        public TickTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void onTick(long ticks) {
            this.task.run();
        }
    }
}
