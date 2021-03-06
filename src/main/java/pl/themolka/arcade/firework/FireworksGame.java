/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.firework;

import com.google.common.collect.ImmutableMap;
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
                    this.handlers.add(constructor.newInstance(((Ref) result)));
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
