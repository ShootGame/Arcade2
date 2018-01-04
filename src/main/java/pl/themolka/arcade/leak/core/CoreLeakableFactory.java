package pl.themolka.arcade.leak.core;

import org.bukkit.Material;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.goal.GoalHolder;
import pl.themolka.arcade.leak.LeakGame;
import pl.themolka.arcade.leak.LeakableFactory;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLMaterial;
import pl.themolka.arcade.xml.XMLParser;

import java.util.Collections;
import java.util.List;

public class CoreLeakableFactory implements LeakableFactory<CoreLeakable> {
    @Override
    public CoreLeakable newLeakable(LeakGame game, String id, GoalHolder owner, Element xml) throws JDOMException {
        String paramLiquid = xml.getAttributeValue("liquid");
        String paramMaterial = xml.getAttributeValue("material");
        String paramDetectorLevel = xml.getAttributeValue("detector-level");
        Element regionElement = xml.getChildren().get(0);

        // liquid
        Liquid liquid = CoreLeakable.DEFAULT_LIQUID;
        if (paramLiquid != null && !paramLiquid.isEmpty()) {
            Liquid type = Liquid.valueOf(XMLParser.parseEnumValue(paramLiquid));
            if (type != null) {
                liquid = type;
            }
        }

        // material
        List<Material> material = Collections.singletonList(CoreLeakable.DEFAULT_MATERIAL);
        if (paramMaterial != null) {
            material = XMLMaterial.parseArray(new Attribute("material", paramMaterial), CoreLeakable.DEFAULT_MATERIAL);
        }

        // detector
        int detectorLevel = CoreLeakable.DEFAULT_DETECTOR_LEVEL;
        if (paramDetectorLevel != null && !paramDetectorLevel.isEmpty()) {
            try {
                detectorLevel = Integer.parseInt(paramDetectorLevel);
            } catch (NumberFormatException ignored) {
            }
        }

        // region
        Region region = regionElement != null ? XMLRegion.parse(game.getGame().getMap(), regionElement) : null;
        if (region == null) {
            return null;
        }

        CoreLeakable core = new CoreLeakable(game, owner, id);
        core.setMaterial(material);
        core.build(liquid, region, detectorLevel);
        return core;
    }
}
