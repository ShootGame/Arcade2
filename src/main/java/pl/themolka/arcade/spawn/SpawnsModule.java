package pl.themolka.arcade.spawn;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.ModuleVersion;

import java.util.HashMap;
import java.util.Map;

@ModuleInfo(id = "Spawns")
@ModuleVersion("1.0")
public class SpawnsModule extends Module<SpawnsGame> {
    @Override
    public SpawnsGame buildGameModule(Element xml, Game game) throws JDOMException {
        Map<String, Spawn> spawns = new HashMap<>();

        for (Element child : xml.getChildren()) {
            String id = child.getAttributeValue("id");
            if (id != null) {
                id = id.trim();
            }

            if (id != null && !id.isEmpty()) {
                Spawn spawn = XMLSpawn.parse(game.getMap(), child);
                if (spawn != null) {
                    spawns.put(id, spawn);
                }
            }
        }

        return spawns.isEmpty() ? null : new SpawnsGame(spawns);
    }
}
