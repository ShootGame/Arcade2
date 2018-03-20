package pl.themolka.arcade.spawn;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

public class XMLSpawn extends XMLParser {
    public static Spawn parse(Game game, Element xml) {
        switch (xml.getName().toLowerCase()) {
            case "multi":
                return parseMulti(game, xml);
            case "region":
                return parseRegionUnion(game, xml);
            default:
                return parseRegionExact(game, xml);
        }
    }

    public static MultiSpawn parseMulti(Game game, Element xml) {
        List<Spawn> spawns = new ArrayList<>();
        for (Element child : xml.getChildren()) {
            Spawn spawn = parse(game, child);

            if (spawn != null) {
                spawns.add(spawn);
            }
        }

        return spawns.isEmpty() ? null : MultiSpawn.of(spawns);
    }

    public static RegionSpawnVector parseRegion(Game game, Element xml, Region region) {
        if (region == null) {
            return null;
        }

        RegionSpawnVector spawn = new RegionSpawnVector(region);

        Attribute yawAttr = xml.getAttribute("yaw");
        if (yawAttr != null) {
            spawn.setYaw((float) parseDouble(yawAttr.getValue(), 180F));
        }

        Attribute pitchAttr = xml.getAttribute("pitch");
        if (pitchAttr != null) {
            spawn.setPitch((float) parseDouble(pitchAttr.getValue(), 0F));
        }

        return spawn;
    }

    public static RegionSpawnVector parseRegionExact(Game game, Element xml) {
        Region region = XMLRegion.parse(game, xml);
        return region != null ? parseRegion(game, xml, region) : null;
    }

    public static RegionSpawnVector parseRegionUnion(Game game, Element xml) {
        Region region = XMLRegion.parseUnion(game, xml);
        return region != null ? parseRegion(game, xml, region) : null;
    }
}
