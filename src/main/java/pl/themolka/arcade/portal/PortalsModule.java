package pl.themolka.arcade.portal;

import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.spawn.SpawnsModule;

@ModuleInfo(id = "Portals",
        loadBefore = {
                FiltersModule.class,
                KitsModule.class,
                SpawnsModule.class})
public class PortalsModule extends Module<PortalsGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(PortalsGameParser.class);
    }
}
