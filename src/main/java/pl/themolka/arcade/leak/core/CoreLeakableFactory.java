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
    public CoreLeakable newLeakable(LeakGame game, GoalHolder owner, String id, String name, Element xml) throws JDOMException {
        return this.parseCoreXml(game, name, xml, new CoreLeakable(game, owner, id));
    }

    public CoreLeakable parseCoreXml(LeakGame game, String name, Element xml, CoreLeakable core) {
        String paramLiquid = xml.getAttributeValue("liquid");
        String paramMaterial = xml.getAttributeValue("material");
        String paramDetectorLevel = xml.getAttributeValue("detector-level");

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
        Region region = XMLRegion.parse(game.getGame().getMap(), xml); // its children are regions
        if (region == null) {
            return null;
        }

        // setup
        core.setMaterial(material);
        core.build(liquid, region, detectorLevel);
        return core;
    }
}
