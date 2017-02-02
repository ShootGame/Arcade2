package pl.themolka.arcade.leak;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.team.TeamsModule;

@ModuleInfo(id = "leak", dependency = {MatchModule.class}, loadBefore = {TeamsModule.class})
public class LeakModule extends Module<LeakGame> {
    @Override
    public LeakGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new LeakGame();
    }
}
