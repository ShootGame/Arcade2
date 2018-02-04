package pl.themolka.arcade.team.apply;

import org.bukkit.Location;
import org.jdom2.Element;
import pl.themolka.arcade.map.ArcadeMap;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.UnionRegion;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLParser;

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
        UnionRegion region = XMLRegion.parseUnion(map, xml);
        if (region.isEmpty()) {
            return null;
        }

        float yaw = XMLParser.parseFloat(xml.getAttributeValue("yaw"), TeamSpawnApply.DEFAULT_YAW);
        float pitch = XMLParser.parseFloat(xml.getAttributeValue("pitch"), TeamSpawnApply.DEFAULT_PITCH);

        return new RegionSpawnApply(region, yaw, pitch);
    }
}
