package pl.themolka.arcade.life;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

@ModuleInfo(id = "Kill-Rewards",
        loadBefore = {
                FiltersModule.class,
                KitsModule.class})
@ModuleVersion("1.0")
public class KillRewardsModule extends Module<KillRewardsGame> {
    @Override
    public KillRewardsGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new KillRewardsGame();
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(KillRewardsGameParser.class);
    }
}
