package pl.themolka.arcade.task;

public interface CountdownListener extends TaskListener {
    void onDone();

    void onUpdate(long seconds, long secondsLeft);
}
