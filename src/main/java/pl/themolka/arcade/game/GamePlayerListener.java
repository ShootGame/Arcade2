package pl.themolka.arcade.game;

public interface GamePlayerListener {
    void notifyRegister();

    void notifyRegistered();

    void notifyUnregister();

    void notifyUnregistered();
}
