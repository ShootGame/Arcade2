package pl.themolka.arcade.firework;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.objective.core.CoreLeakFireworks;
import pl.themolka.arcade.objective.point.PointCaptureFireworks;
import pl.themolka.arcade.objective.wool.WoolPlaceFireworks;

import java.util.ArrayList;
import java.util.List;

public class FireworksGame extends GameModule {
    private final List<FireworkHandler> handlers = new ArrayList<>();

    protected FireworksGame(Config config) {
        this.handlers.add(new CoreLeakFireworks(config.onCoreLeak().get()));
        this.handlers.add(new PointCaptureFireworks(config.onPointCapture().get()));
        this.handlers.add(new WoolPlaceFireworks(config.onWoolPlace().get()));
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
        default Ref<Boolean> onCoreLeak() { return Ref.ofProvided(true); }
        default Ref<Boolean> onPointCapture() { return Ref.ofProvided(true); }
        default Ref<Boolean> onWoolPlace() { return Ref.ofProvided(true); }

        @Override
        default FireworksGame create(Game game, Library library) {
            return new FireworksGame(this);
        }
    }
}
