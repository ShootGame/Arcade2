package pl.themolka.arcade.firework;

import pl.themolka.arcade.capture.point.PointCaptureFireworks;
import pl.themolka.arcade.capture.wool.WoolPlaceFireworks;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.leak.core.CoreLeakFireworks;

import java.util.ArrayList;
import java.util.List;

public class FireworksGame extends GameModule {
    private final List<FireworkHandler> handlers = new ArrayList<>();

    @Deprecated
    public FireworksGame(List<FireworkHandler> handlers) {
        this.handlers.addAll(handlers);
    }

    protected FireworksGame(Config config) {
        this.handlers.add(new CoreLeakFireworks(config.onCoreLeak()));
        this.handlers.add(new PointCaptureFireworks(config.onPointCapture()));
        this.handlers.add(new WoolPlaceFireworks(config.onWoolPlace()));
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

    public interface Config extends IGameModuleConfig<FireworksGame> {
        default boolean onCoreLeak() { return true; }
        default boolean onPointCapture() { return true; }
        default boolean onWoolPlace() { return true; }

        @Override
        default FireworksGame create(Game game) {
            return new FireworksGame(this);
        }
    }
}
