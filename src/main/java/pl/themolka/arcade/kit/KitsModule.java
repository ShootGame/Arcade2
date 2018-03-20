package pl.themolka.arcade.kit;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "Kits")
public class KitsModule extends Module<KitsGame> {
    @Override
    public KitsGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<Kit> kits = new ArrayList<>();
        for (Element kitElement : xml.getChildren("kit")) {
            Kit kit = XMLKit.parse(this.getPlugin(), kitElement);
            if (kit != null) {
                kits.add(kit);
            }
        }

        return kits.isEmpty() ? null : new KitsGame(kits);
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(KitsGameParser.class);
    }
}
