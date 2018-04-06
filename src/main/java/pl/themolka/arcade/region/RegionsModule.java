package pl.themolka.arcade.region;

import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Regions",
        loadBefore = {
                FiltersModule.class})
public class RegionsModule extends Module<RegionsGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(RegionsGameParser.class);
    }

    @Override
    public void defineGameModule(Game game, IGameModuleConfig<RegionsGame> config, IGameConfig.Library library, ConfigContext context) {
        context.define(GlobalRegion.REGION_ID, new GlobalRegion.Config() {});
    }
}
