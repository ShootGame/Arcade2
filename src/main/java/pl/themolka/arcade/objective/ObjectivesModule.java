package pl.themolka.arcade.objective;

import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Objectives",
        dependency = {
                MatchModule.class})
@ModuleVersion("1.0")
public class ObjectivesModule extends Module<ObjectivesGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(ObjectivesGameParser.class);
    }
}
