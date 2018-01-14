package pl.themolka.arcade.firework;

import pl.themolka.arcade.game.GameModule;

import java.util.ArrayList;
import java.util.List;

public class FireworksGame extends GameModule {
    private final List<FireworkHandler> handlers;

    public FireworksGame(List<FireworkHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onEnable() {
        for (FireworkHandler handler : this.handlers) {
            try {
                handler.onEnable(this);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        if (!this.handlers.isEmpty()) {
            register.addAll(this.handlers);
        }

        return register;
    }

    public List<FireworkHandler> getHandlers() {
        return new ArrayList<>(this.handlers);
    }
}
