package pl.themolka.arcade.map;

import com.google.common.eventbus.Subscribe;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

import java.io.File;
import java.io.IOException;

@ModuleInfo(id = "map-loader")
public class MapLoaderModule extends Module<Object> {
    private final MapManager maps = this.getPlugin().getMaps();

    @Override
    public void onEnable(Element global) throws JDOMException {
        if (global == null) {
            throw new JDOMException("global <" + this.getId() + "> not set");
        }
    }

    @Subscribe
    public void onMapContainerFill(MapContainerFillEvent event) {
    }

    private OfflineMap readMapDirectory(File directory) throws IOException, MapParserException {
        MapParser.Technology technology = this.maps.getParser();
        File file = new File(directory, technology.getDefaultFilename());

        MapParser parser = technology.newInstance();
        parser.readFile(file);

        OfflineMap map = parser.parseOfflineMap();
        map.setDirectory(directory);
        map.setSettings(file);

        return map;
    }
}
