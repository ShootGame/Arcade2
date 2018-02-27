package pl.themolka.arcade.capture.flag;

import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.CapturableFactory;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.FiltersGame;
import pl.themolka.arcade.filter.FiltersModule;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.region.Region;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.region.XMLRegion;
import pl.themolka.arcade.xml.XMLParser;

public class FlagFactory implements CapturableFactory<Flag> {
    @Override
    public Flag newCapturable(CaptureGame game, Participator owner, String id, String name, Element xml) throws JDOMException {
        return this.parseFlagXml(game, xml, new Flag(game, owner, id));
    }

    public Flag parseFlagXml(CaptureGame game, Element xml, Flag flag) {
        // capture
        for (Element captureElement : xml.getChildren("capture")) {
            FlagCapture capture = this.parseFlagCapture(game, captureElement, new FlagCapture(game, flag));
            if (capture != null) {
                flag.addCapture(capture);
            }
        }

        // spawn and banner item
        Banner banner = null;
        for (Element spawnElement : xml.getChildren("spawn")) {
            FlagSpawn spawn = this.parseFlagSpawn(game, spawnElement, new FlagSpawn(game, flag));
            if (spawn != null) {
                flag.addSpawn(spawn);

                banner = findBannerIn(spawn.getRegion());

                float yaw = (float) XMLParser.parseInt(xml.getAttributeValue("yaw"), Integer.MAX_VALUE);
                if (yaw > 180 || yaw < -180) {
                    if (banner == null) {
                        continue;
                    } else {
                        yaw = banner.getBlock().getLocation().getYaw();
                    }
                }

                spawn.setBanner(banner);
                spawn.setDirection(BlockFace.byYaw(yaw));
            }
        }

        if (banner == null || flag.getSpawns().isEmpty()) {
            return null;
        }

        // setup
        flag.getItem().transferMetaFrom(banner);
        flag.setObjective(XMLParser.parseInt(xml.getAttributeValue("objective"), Flag.NOT_OBJECTIVE));
        return flag;
    }

    public FlagCapture parseFlagCapture(CaptureGame game, Element xml, FlagCapture capture) {
        Region region = XMLRegion.parseUnion(game.getGame(), xml);
        if (region == null) {
            return null;
        }

        capture.setFieldStrategy(this.findRegionFieldStrategy(xml, FlagCapture.DEFAULT_FIELD_STRATEGY));
        capture.setFilter(this.findFilter(game.getGame(), xml.getAttributeValue("filter"), FlagCapture.DEFAULT_FILTER));
        capture.setRegion(region);
        return capture;
    }

    public FlagSpawn parseFlagSpawn(CaptureGame game, Element xml, FlagSpawn spawn) {
        Region region = XMLRegion.parseUnion(game.getGame(), xml);
        if (region == null) {
            return null;
        }

        spawn.setFieldStrategy(this.findRegionFieldStrategy(xml, FlagSpawn.DEFAULT_FIELD_STRATEGY));
        spawn.setFilter(this.findFilter(game.getGame(), xml.getAttributeValue("filter"), FlagSpawn.DEFAULT_FILTER));
        spawn.setRegion(region);
        return spawn;
    }

    private Filter findFilter(Game game, String id, Filter def) {
        if (id != null && !id.trim().isEmpty()) {
            FiltersGame filters = (FiltersGame) game.getModule(FiltersModule.class);
            if (filters != null) {
                return filters.filterOrDefault(id, def);
            }
        }

        return def;
    }

    private RegionFieldStrategy findRegionFieldStrategy(Element xml, RegionFieldStrategy def) {
        return xml != null ? RegionFieldStrategyValues.of(xml.getAttributeValue("field"), def) : def;
    }

    private enum RegionFieldStrategyValues {
        EVERYWHERE(RegionFieldStrategy.EVERYWHERE), // everywhere
        EXACT(RegionFieldStrategy.EXACT), // compare exact (double) locations
        NET(RegionFieldStrategy.NET), // compare block (int) locations
        NET_ROUND(RegionFieldStrategy.NET_ROUND), // compare center block locations
        NOWHERE(RegionFieldStrategy.NOWHERE), // nowhere
        ;

        final RegionFieldStrategy strategy;

        RegionFieldStrategyValues(RegionFieldStrategy strategy) {
            this.strategy = strategy;
        }

        static RegionFieldStrategy of(String value, RegionFieldStrategy def) {
            if (value != null) {
                try {
                    return valueOf(XMLParser.parseEnumValue(value)).strategy;
                } catch (IllegalArgumentException ignored) {
                }
            }

            return def;
        }
    }

    public static Banner findBannerIn(Region region) {
        for (Block block : region.getBlocks()) {
            if (BannerUtils.isBanner(block)) {
                return (Banner) block.getState();
            }
        }

        return null;
    }
}
