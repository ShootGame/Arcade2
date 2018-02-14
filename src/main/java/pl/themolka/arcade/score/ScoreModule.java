package pl.themolka.arcade.score;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.team.TeamsModule;
import pl.themolka.arcade.xml.XMLParser;

import java.util.HashMap;
import java.util.Map;

@ModuleInfo(id = "Score",
        dependency = {
                MatchModule.class},
        loadBefore = {
                FiltersModule.class,
                TeamsModule.class})
public class ScoreModule extends Module<ScoreGame> {
    @Override
    public ScoreGame buildGameModule(Element xml, Game game) throws JDOMException {
        Map<String, Element> competitors = new HashMap<>();
        for (Element competitor : XMLParser.children(xml, "participator", "competitor", "team")) {
            String id = competitor.getValue();

            if (id != null) {
                competitors.put(id.trim(), competitor);
            }
        }

        return new ScoreGame(ScoreConfig.parse(xml, ScoreConfig.NULL_CONFIG), competitors);
    }
}
