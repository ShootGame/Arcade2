package pl.themolka.arcade.score;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.team.TeamsModule;
import pl.themolka.arcade.xml.XMLParser;

@ModuleInfo(id = "Score",
        dependency = {
                MatchModule.class},
        loadBefore = {
                TeamsModule.class})
public class ScoreModule extends Module<ScoreGame> {
    public static final double LIMIT_NULL = Score.MAX;

    @Override
    public ScoreGame buildGameModule(Element xml, Game game) throws JDOMException {
        String name = xml.getAttributeValue("name");
        if (name != null) {
            name = name.trim();
        }

        ScoreGame module = new ScoreGame(XMLParser.parseDouble(xml.getAttributeValue("limit"), LIMIT_NULL), name);
        module.setDeathLoss(XMLParser.parseDouble(xml.getAttributeValue("death-loss"), Score.ZERO));
        module.setKillReward(XMLParser.parseDouble(xml.getAttributeValue("kill-reward"), Score.ZERO));
        return module;
    }
}
