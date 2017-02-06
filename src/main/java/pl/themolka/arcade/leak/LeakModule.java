package pl.themolka.arcade.leak;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.team.TeamsModule;

@ModuleInfo(id = "Leak",
        dependency = {
                MatchModule.class},
        loadBefore = {
                TeamsModule.class})
@ModuleVersion("1.0")
public class LeakModule extends Module<LeakGame> {
    @Override
    public LeakGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new LeakGame();
    }
}
