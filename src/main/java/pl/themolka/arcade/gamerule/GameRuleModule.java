package pl.themolka.arcade.gamerule;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "gamerules")
public class GameRuleModule extends Module<GameRuleGame> {
    public static final String METADATA_GAMERULES = "gamerules";

    @Override
    public GameRuleGame buildGameModule(Element xml) throws JDOMException {
        List<GameRule> rules = new ArrayList<>();
        for (Element element : xml.getChildren("gamerule")) {
            GameRuleType type = GameRuleType.forName(element.getAttributeValue("type"));

            if (type != null) {
                rules.add(new GameRule(type, element.getTextNormalize()));
            }
        }

        return new GameRuleGame(rules);
    }
}
