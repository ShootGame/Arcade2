package pl.themolka.arcade.filter;

import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Filters")
public class FiltersModule extends Module<FiltersGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(FiltersGameParser.class);
    }

    @Override
    public void defineGameModule(Game game, IGameModuleConfig<FiltersGame> config, IGameConfig.Library library, ConfigContext context) {
        context.define("allow", StaticFilter.ALLOW.config());
        context.define("deny", StaticFilter.DENY.config());
        context.define("abstain", StaticFilter.ABSTAIN.config());
    }
}
