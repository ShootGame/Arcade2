package pl.themolka.arcade.life;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.kit.KitsModule;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;

@ModuleInfo(id = "Kill-Reward",
        loadBefore = {
                FiltersModule.class,
                KitsModule.class})
@ModuleVersion("1.0")
public class KillRewardModule extends Module<KillRewardGame> {
    @Override
    public KillRewardGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new KillRewardGame();
    }
}
