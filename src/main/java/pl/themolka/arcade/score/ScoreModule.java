package pl.themolka.arcade.score;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.match.MatchModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.team.TeamsModule;

@ModuleInfo(id = "Score",
        dependency = {
                MatchModule.class},
        loadBefore = {
                TeamsModule.class})
public class ScoreModule extends Module<ScoreGame> {
    public static final int LIMIT_NULL = Integer.MAX_VALUE;

    @Override
    public ScoreGame buildGameModule(Element xml, Game game) throws JDOMException {
        int kills = 1;
        Element killsElement = xml.getChild("kills");
        if (killsElement != null) {
            try {
                int value = Integer.parseInt(killsElement.getTextNormalize());
                if (value >= 0) {
                    kills = value;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        int limit = LIMIT_NULL;
        Element limitElement = xml.getChild("limit");
        if (limitElement != null) {
            try {
                limit = Integer.parseInt(limitElement.getTextNormalize());
            } catch (NumberFormatException ignored) {
            }
        }

        String name = null;
        Element nameElement = xml.getChild("name");
        if (nameElement != null) {
            name = nameElement.getTextNormalize();
        }

        return new ScoreGame(kills, limit, name);
    }
}
