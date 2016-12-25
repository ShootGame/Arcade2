package pl.themolka.arcade.game;

import java.util.List;

public interface GameEvents {
    List<Object> onListenersRegister(List<Object> register);
}
