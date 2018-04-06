package pl.themolka.arcade.leak.core;

import org.bukkit.Material;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.leak.LeakGame;
import pl.themolka.arcade.leak.LeakableFactory;
import pl.themolka.arcade.leak.Liquid;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.xml.XMLParser;
import pl.themolka.arcade.xml.XMLRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoreFactory implements LeakableFactory<Core> {
    @Override
    public Core newLeakable(LeakGame game, Participator owner, String id, String name, Element xml) throws JDOMException {
        return this.parseCoreXml(game, xml, new Core(game, owner, id));
    }

    public Core parseCoreXml(LeakGame game, Element xml, Core core) {
        String paramLiquid = xml.getAttributeValue("liquid");
        String paramMaterial = xml.getAttributeValue("material");
        String paramDetectorLevel = xml.getAttributeValue("detector-level");

        // liquid
        Liquid liquid = Core.DEFAULT_LIQUID;
        if (paramLiquid != null && !paramLiquid.isEmpty()) {
            Liquid type = Liquid.valueOf(XMLParser.parseEnumValue(paramLiquid));
            if (type != null) {
                liquid = type;
            }
        }

        // material
        List<Material> material = Collections.singletonList(Core.DEFAULT_MATERIAL);
        if (paramMaterial != null) {
            material = parseArray(new Attribute("material", paramMaterial), Core.DEFAULT_MATERIAL);
        }

        // detector
        int detectorLevel = Core.DEFAULT_DETECTOR_LEVEL;
        if (paramDetectorLevel != null && !paramDetectorLevel.isEmpty()) {
            try {
                detectorLevel = Integer.parseInt(paramDetectorLevel);
            } catch (NumberFormatException ignored) {
            }
        }

        // region
        Region region = XMLRegion.parseUnion(game.getGame(), xml.getChild("region"));
        if (region == null) {
            return null;
        }

        // setup
        core.setMaterial(material);
        core.build(liquid, region, detectorLevel);
        return core;
    }

    public static List<Material> parseArray(Attribute attribute, Material def) {
        List<Material> results = new ArrayList<>();
        if (attribute != null) {
            List<String> rawList = ParserUtils.array(attribute.getValue());
            for (String raw : rawList) {
                if (raw.isEmpty()) {
                    continue;
                }

                Material material = Material.matchMaterial(XMLParser.parseEnumValue(raw));
                if (material != null) {
                    results.add(material);
                }
            }
        } else if (def != null) {
            results.add(def);
        }

        return results;
    }
}
