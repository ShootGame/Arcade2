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
