package pl.themolka.arcade.module;

import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public class SimpleGlobalModule extends Module<GameModule> {
    @Override
    public final GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return null;
    }

    @Override
    public final void defineGameModule(Game game, IGameModuleConfig<GameModule> config, IGameConfig.Library library, ConfigContext context) {
    }

    @Override
    public final GameModule createGameModule(Game game, IGameModuleConfig<GameModule> config, IGameConfig.Library library, ConfigContext context) {
        return null;
    }
}
