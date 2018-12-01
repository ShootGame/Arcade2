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

package pl.themolka.arcade.module;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameHolder;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.util.StringId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * @deprecated Modules became unnecessary when I wrote the new parser system.
 * All {@code GameModule}s are being parsed and instantiated using the parsers.
 * I still want to have a way to create commands and such (which must be
 * global), probably in the services.
 */
@Deprecated
public class Module<GM extends GameModule> extends SimpleModuleListener
                       implements GameHolder, Listener, StringId {
    private ArcadePlugin plugin;

    private String id;
    private Class<? extends Module<?>>[] dependency;
    private Class<? extends Module<?>>[] loadBefore;
    private boolean global;
    private final List<Object> listenerObjects = new CopyOnWriteArrayList<>();
    private boolean loaded = false;
    private String name;

    public Module() {
    }

    public final void initialize(ArcadePlugin plugin) {
        if (this.isLoaded()) {
            return;
        }

        this.loaded = true;
        this.plugin = plugin;

        ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
        if (info == null || info.id() == null || info.id().isEmpty()) {
            throw new RuntimeException("Module must be @ModuleInfo decorated");
        }

        this.id = info.id().toLowerCase();
        this.dependency = info.dependency();
        this.loadBefore = info.loadBefore();
        this.name = info.id();
    }

    @Override
    public Game getGame() {
        return this.plugin.getGames().getCurrentGame();
    }

    @Override
    public final String getId() {
        return this.id;
    }

    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return null;
    }

    public void defineGameModule(Game game, IGameModuleConfig<GM> config, IGameConfig.Library library, ConfigContext context) {
    }

    public GM createGameModule(Game game, IGameModuleConfig<GM> config, IGameConfig.Library library, ConfigContext context) {
        return config != null ? library.getOrDefine(game, config) : null;
    }

    public final void destroy() {
        this.unregisterListenerObject(this);
        for (Object listener : this.getListenerObjects()) {
            this.unregisterListenerObject(listener);
        }
    }

    public Class<? extends Module<?>>[] getDependency() {
        return this.dependency;
    }

    public GM getGameModule() {
        Game game = this.plugin.getGames().getCurrentGame();
        if (game != null) {
            return (GM) game.getModules().getModuleById(this.getId());
        }

        return null;
    }

    public List<Object> getListenerObjects() {
        return this.listenerObjects;
    }

    public Class<? extends Module<?>>[] getLoadBefore() {
        return this.loadBefore;
    }

    public Logger getLogger() {
        return this.getPlugin().getLogger();
    }

    public String getName() {
        return this.name;
    }

    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    public Server getServer() {
        return this.getPlugin().getServer();
    }

    public boolean isGameModuleEnabled() {
        return this.getGame() != null && this.getGameModule() != null;
    }

    public boolean isGlobal() {
        return this.global;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void registerCommandObject(Object object) {
        this.plugin.registerCommandObject(object);
    }

    public boolean registerListenerObject(Object object) {
        boolean result = this.listenerObjects.add(object);
        if (result) {
            this.getPlugin().registerListenerObject(object);
        }

        return result;
    }

    public void registerListeners() {
        if (!this.getListenerObjects().isEmpty()) {
            return;
        }

        List<Object> listeners = this.onListenersRegister(new ArrayList<>());
        if (listeners != null) {
            for (Object listener : listeners) {
                this.registerListenerObject(listener);
            }
        }

        this.registerListenerObject(this);
    }

    public void setDependency(Class<? extends Module<?>>[] dependency) {
        this.dependency = dependency;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public void setLoadBefore(Class<? extends Module<?>>[] loadBefore) {
        this.loadBefore = loadBefore;
    }

    public boolean unregisterListenerObject(Object object) {
        this.getPlugin().unregisterListenerObject(object);
        return this.listenerObjects.remove(object);
    }
}
