package pl.themolka.arcade.spawn;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLSpawn extends XMLParser {
    public static Spawn parse(ArcadeMap map, Element xml) {
        switch (xml.getName().toLowerCase()) {
            case "multi":
                return parseMulti(map, xml);
            case "region":
                return parseRegion(map, xml);
            default:
                return null;
        }
    }

    public static MultiSpawn parseMulti(ArcadeMap map, Element xml) {
        List<Spawn> spawns = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Spawn spawn = parse(map, child);

            if (spawn != null) {
                spawns.add(spawn);
            }
        }

        return spawns.isEmpty() ? null : MultiSpawn.of(spawns);
    }

    public static RegionSpawnVector parseRegion(ArcadeMap map, Element xml) {
        Region region = XMLRegion.parseUnion(map, xml);
        if (region == null) {
            return null;
        }

        RegionSpawnVector spawn = new RegionSpawnVector(region);

        Attribute yawAttr = xml.getAttribute("yaw");
        if (yawAttr != null) {
            spawn.setYaw(parseFloat(yawAttr.getValue()));
        }

        Attribute pitchAttr = xml.getAttribute("pitch");
        if (pitchAttr != null) {
            spawn.setPitch(parseFloat(pitchAttr.getValue()));
        }

        return spawn;
    }
}
