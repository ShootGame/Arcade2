package pl.themolka.arcade.task;

public interface CountdownListener extends TaskListener {
    boolean isCancelable();

    void onCancel();

    void onDone();

    void onUpdate(long seconds, long secondsLeft);
}
