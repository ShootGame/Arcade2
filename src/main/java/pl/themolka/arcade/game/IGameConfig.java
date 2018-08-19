package pl.themolka.arcade.game;

import pl.themolka.arcade.config.IConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface IGameConfig<T> extends IConfig {
    /**
     * Create a new T object based on this config.
     * @param game The game this object is created in.
     * @param library The library this object will be created in.
     * @return The object based on this config, or {@code null}.
     */
    T create(Game game, Library library);

    class Library {
        public static final Library EMPTY = new EmptyLibrary();

        private final Map<IGameConfig<?>, Object> definitionMap = new HashMap<>();

        public boolean contains(IGameConfig<?> config) {
            return config != null && this.definitionMap.containsKey(config);
        }

        public boolean contains(Object object) {
            return object != null && this.definitionMap.containsValue(object);
        }

        public <T> T define(Game game, IGameConfig<T> config) throws AlreadyDefined {
            return this.define(game, config, false);
        }

        public <T> T define(Game game, IGameConfig<T> config, boolean replace) throws AlreadyDefined {
            if (config == null) {
                return null;
            }

            return this.define(new DefinitionCreator<>(game, config), replace);
        }

        public <T> T define(IGameConfig<T> config, T t) throws AlreadyDefined {
            return this.define(config, t, false);
        }

        public <T> T define(IGameConfig<T> config, T t, boolean replace) throws AlreadyDefined {
            if (config == null || t == null) {
                return null;
            } else if (!replace && this.contains(config)) {
                throw new AlreadyDefined(config);
            }

            return this.write(config, t);
        }

        public <T> T get(IGameConfig<T> config) {
            return this.getOrDefault(config, null);
        }

        public <T> T getOrDefault(IGameConfig<?> config, T def) {
            return (T) this.definitionMap.getOrDefault(config, def);
        }

        public <T> T getOrDefine(Game game, IGameConfig<T> config) {
            if (config == null) {
                return null;
            }

            return this.getOrDefine(new DefinitionCreator<>(game, config));
        }

        public <T> T getOrDefine(IGameConfig<T> config, T t) {
            if (config == null) {
                return null;
            }

            Objects.requireNonNull(t, "object cannot be null");
            try {
                return this.contains(config) ? this.get(config)
                                             : this.define(config, t, false);
            } catch (AlreadyDefined shouldNeverHappen) {
                throw new Error(shouldNeverHappen);
            }
        }

        public <T> T unset(IGameConfig<T> config) {
            T unset = this.get(config);
            this.write(config, null);
            return unset;
        }

        //
        // Helpers
        //

        protected <T> T define(DefinitionCreator<T> factory, boolean replace) throws AlreadyDefined {
            Objects.requireNonNull(factory, "factory cannot be null");
            return this.define(factory.config, factory.create(), replace);
        }

        protected <T> T getOrDefine(DefinitionCreator<T> factory) {
            Objects.requireNonNull(factory, "factory cannot be null");
            try {
                return this.contains(factory.config) ? this.get(factory.config)
                                                     : this.define(factory.config, factory.create());
            } catch (AlreadyDefined shouldNeverHappen) {
                throw new Error(shouldNeverHappen);
            }
        }

        protected <T> T write(IGameConfig<T> key, T value) {
            Objects.requireNonNull(key, "key cannot be null");
            if (value != null) {
                this.definitionMap.put(key, value);
            } else {
                this.definitionMap.remove(key);
            }

            return value;
        }

        protected class DefinitionCreator<T> {
            protected final Game game;
            protected final IGameConfig<T> config;

            protected DefinitionCreator(Game game, IGameConfig<T> config) {
                this.game = Objects.requireNonNull(game, "game cannot be null");
                this.config = Objects.requireNonNull(config, "config cannot be null");
            }

            protected T create() {
                return this.config.create(this.game, Library.this);
            }
        }
    }

    class AlreadyDefined extends Exception {
        public AlreadyDefined(IGameConfig<?> config) {
            super(config.getClass().getCanonicalName() + " is already defined");
        }
    }

    final class EmptyLibrary extends Library {
        private EmptyLibrary() {
        }

        @Override
        protected <T> T write(IGameConfig<T> key, T value) {
            return value;
        }
    }
}
