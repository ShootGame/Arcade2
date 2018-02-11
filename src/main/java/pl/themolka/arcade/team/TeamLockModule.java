package pl.themolka.arcade.team;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "Team-Lock",
        dependency = {
                FiltersModule.class,
                TeamsModule.class})
public class TeamLockModule extends Module<TeamLockGame> {
    @Override
    public TeamLockGame buildGameModule(Element xml, Game game) throws JDOMException {
        GameModule filters = this.getGame().getModule(FiltersModule.class);
        if (filters == null) {
            return null;
        }

        GameModule teams = this.getGame().getModule(TeamsModule.class);
        if (teams == null) {
            return null;
        }

        FiltersGame filtersGame = (FiltersGame) teams;
        TeamsGame teamsGame = (TeamsGame) teams;

        List<TeamLockRule> rules = new ArrayList<>();
        for (Element child : xml.getChildren("rule")) {
            Filter filter = filtersGame.getFilter(child.getAttributeValue("filter"));
            String message = XMLParser.parseMessage(xml.getValue());

            TeamLockRule rule = new TeamLockRule(filter, message);
            rules.add(rule);
        }

        return new TeamLockGame(rules, teamsGame);
    }
}
