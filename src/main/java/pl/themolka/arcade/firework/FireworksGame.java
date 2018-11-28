package pl.themolka.arcade.firework;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.objective.core.CoreLeakFireworks;
import pl.themolka.arcade.objective.point.PointCaptureFireworks;
import pl.themolka.arcade.objective.wool.WoolPlaceFireworks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FireworksGame extends GameModule {
    private static final Map<Class<? extends FireworkHandler>, String> handlerMap = ImmutableMap.<Class<? extends FireworkHandler>, String>builder()
            .put(CoreLeakFireworks.class, "onCoreLeak")
            .put(PointCaptureFireworks.class, "onPointCapture")
            .put(WoolPlaceFireworks.class, "onWoolPlace")
            .build();

    private final List<FireworkHandler> handlers = new ArrayList<>();

    protected FireworksGame(Config config) {
        for (Map.Entry<Class<? extends FireworkHandler>, String> handler : handlerMap.entrySet()) {
            Class<? extends FireworkHandler> clazz = handler.getKey();

            try {
                Method enabled = config.getClass().getMethod(handler.getValue());
                enabled.setAccessible(true);

                Constructor<? extends FireworkHandler> constructor = clazz.getConstructor(Ref.class);
                constructor.setAccessible(true);

                Object result = enabled.invoke(config);
                if (result instanceof Ref<?>) {
                    this.handlers.add(constructor.newInstance(((Ref) result).get()));
                } else {
                    throw new ReflectiveOperationException("Illegal return value");
                }
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void cancelDamageFromFireworks(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Firework) {
            Firework firework = (Firework) damager;

            MetadataValue metadata = firework.getMetadata(FireworkUtils.DamageMetadata.KEY, this.getPlugin());
            if (metadata instanceof FireworkUtils.DamageMetadata && !((FireworkUtils.DamageMetadata) metadata).value()) {
                event.setCancelled(true);
            }
        }
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
