package pl.themolka.arcade.module;

import java.util.List;

public interface ModuleListener {
    void onEnable();

    void onDisable();

    List<Object> onListenersRegister(List<Object> register);
}
