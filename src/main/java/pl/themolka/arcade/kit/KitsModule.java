package pl.themolka.arcade.kit;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "kits")
public class KitsModule extends Module<KitsGame> {
    @Override
    public KitsGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<Kit> kits = new ArrayList<>();

        for (Element kitElement : xml.getChildren("kit")) {
            Kit kit = XMLKit.parseKit(this.getPlugin(), kitElement);
            if (kit != null) {
                kits.add(kit);
            }
        }

        return new KitsGame(kits);
    }
}
