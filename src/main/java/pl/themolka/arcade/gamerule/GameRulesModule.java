package pl.themolka.arcade.gamerule;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "GameRules")
@ModuleVersion(value = "1.0")
public class GameRulesModule extends Module<GameRulesGame> {
    public static final String METADATA_GAMERULES = "GameRules";

    @Override
    public GameRulesGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<GameRule> rules = new ArrayList<>();
        for (Element element : xml.getChildren()) {
            GameRuleType type = GameRuleType.forName(element.getName());

            if (type != null) {
                rules.add(new GameRule(type, element.getValue()));
            }
        }

        return new GameRulesGame(rules);
    }
}
