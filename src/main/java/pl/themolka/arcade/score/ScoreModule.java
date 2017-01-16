package pl.themolka.arcade.score;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.team.TeamsModule;

@ModuleInfo(id = "score", dependency = {TeamsModule.class})
public class ScoreModule extends Module<ScoreGame> {
    @Override
    public ScoreGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new ScoreGame();
    }
}
