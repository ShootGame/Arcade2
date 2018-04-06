package pl.themolka.arcade.score;

import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.team.TeamsModule;

@ModuleInfo(id = "Score",
        dependency = {
                MatchModule.class},
        loadBefore = {
                FiltersModule.class,
                TeamsModule.class})
public class ScoreModule extends Module<ScoreGame> {
    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(ScoreGameParser.class);
    }
}
