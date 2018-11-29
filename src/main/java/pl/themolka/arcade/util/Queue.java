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

package pl.themolka.arcade.util;

public class Queue<T> {
    private T[] queue;

    public Queue() {
    }

    public Queue(T... queue) {
        this.setQueue(queue);
    }

    public void add(int index, T value) {
        T[] newQueue = (T[]) new Object[this.getSize() + 1];
        for (int i = 0; i < newQueue.length; i++) {
            if (i == index) {
                newQueue[i] = value;
            } else if (i > index) {
                newQueue[i] = this.getQueue()[i - 1];
            }

            this.setQueue(newQueue);
        }
    }

    public void addFirst(T value) {
        this.add(0, value);
    }

    public void addLast(T value) {
        this.add(this.getSize(), value);
    }

    public T[] getQueue() {
        return this.queue;
    }

    public int getSize() {
        return this.getQueue().length;
    }

    public boolean isEmpty() {
        return this.getSize() == 0;
    }

    public void setQueue(T[] queue) {
        if (queue == null) {
            queue = (T[]) new Object[0];
        }

        this.queue = queue;
    }

    public T take(int index) {
        if (this.isEmpty()) {
            return null;
        }

        T object = this.getQueue()[index];

        T[] newQueue = (T[]) new Object[this.getSize() - 1];
        for (int i = 0; i < this.getSize(); i++) {
            int newIndex = i;
            if (i == index) {
                continue;
            } else if (i > index) {
                newIndex = i - 1;
            }

            newQueue[newIndex] = this.getQueue()[i];
        }

        this.setQueue(newQueue);
        return object;
    }

    public T takeNext() {
        return this.take(0);
    }
}
