package pl.themolka.arcade.mob;

import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Mobs",
        dependency = {
                FiltersModule.class})
@ModuleVersion("1.0")
public class MobsModule extends Module<MobsGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(MobsGameParser.class);
    }
}
