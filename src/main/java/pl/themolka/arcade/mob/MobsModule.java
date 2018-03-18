package pl.themolka.arcade.mob;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.xml.XMLParser;

@ModuleInfo(id = "Mobs",
        dependency = {
                FiltersModule.class})
@ModuleVersion("1.0")
public class MobsModule extends Module<MobsGame> {
    @Override
    public MobsGame buildGameModule(Element xml, Game game) throws JDOMException {
        boolean denyNatural = XMLParser.parseBoolean(xml.getAttributeValue("deny-natural"), false);
        return new MobsGame(denyNatural);
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) {
        return context.of(MobsGameParser.class);
    }
}
