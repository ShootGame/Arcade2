package pl.themolka.arcade.gamerule;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "GameRules")
@ModuleVersion(value = "1.0")
public class GameRulesModule extends Module<GameRulesGame> {
    @Override
    public GameRulesGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<GameRule> rules = new ArrayList<>();
        for (Element element : xml.getChildren()) {
            String key = element.getName();
            GameRuleType type = GameRuleType.byKey(key);

            if (type != null) {
                if (type.isApplicable()) {
                    rules.add(new GameRule(type, element.getValue()));
                }
            } else {
                rules.add(new GameRule(key, element.getValue()));
            }
        }

        return new GameRulesGame(rules);
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(GameRulesGameParser.class);
    }
}
