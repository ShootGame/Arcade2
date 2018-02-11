package pl.themolka.arcade.kit;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

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

        for (Kit kit : kits) {
            for (String inherit : kit.getInherit()) {
                Kit inheritKit = this.findKit(inherit, kits);
                if (inheritKit != null) {
                    kit.addContent(inheritKit);
                }
            }
        }

        return new KitsGame(kits);
    }

    private Kit findKit(String id, List<Kit> source) {
        for (Kit kit : source) {
            if (kit.getId().equals(id)) {
                return kit;
            }
        }

        return null;
    }
}
