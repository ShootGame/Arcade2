package pl.themolka.arcade.team;

import org.bukkit.Location;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.XMLRegion;

import java.util.List;

public class RegionSpawnApply extends TeamSpawnApply {
    private final Region region;

    public RegionSpawnApply(Region region) {
        this.region = region;
    }

    public RegionSpawnApply(Region region, float yaw) {
        super(yaw);
        this.region = region;
    }

    public RegionSpawnApply(Region region, float yaw, float pitch) {
        super(yaw, pitch);
        this.region = region;
    }

    @Override
    public Location getSpawnLocation() {
        return this.getRegion().getRandomVector().toLocation(this.getRegion().getWorld());
    }

    public Region getRegion() {
        return this.region;
    }

    public static RegionSpawnApply parse(ArcadeMap map, Element xml) {
        List<Element> regionElements = xml.getChildren();

        Region region = null;
        if (!regionElements.isEmpty()) {
            region = XMLRegion.parse(map, regionElements.get(0));
        }

        float yaw = 0F;
        Attribute yawAttribute = xml.getAttribute("yaw");
        if (yawAttribute != null) {
            try {
                yaw = yawAttribute.getFloatValue();
            } catch (DataConversionException ignored) {
            }
        }

        float pitch = 0F;
        Attribute pitchAttribute = xml.getAttribute("pitch");
        if (pitchAttribute != null) {
            try {
                pitch = pitchAttribute.getFloatValue();
            } catch (DataConversionException ignored) {
            }
        }

        if (region != null) {
            return new RegionSpawnApply(region, yaw, pitch);
        }

        return null;
    }
}
