package pl.themolka.arcade.map;

import net.engio.mbassy.listener.Handler;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.SimpleGlobalModule;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

@ModuleInfo(id = "map-loader")
public class MapLoaderModule extends SimpleGlobalModule implements MapContainerLoader {
    private final List<File> worldFiles = new ArrayList<>();

    @Override
    public void onEnable(Element global) throws JDOMException {
        for (Element directory : global.getChildren("directory")) {
            String path = directory.getAttributeValue("path");
            List<Element> exclude = directory.getChildren("exclude");
            List<Element> include = directory.getChildren("include");

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!exclude.isEmpty()) {
                        for (Element element : exclude) {
                            if (name.equals(element.getTextNormalize())) {
                                return false;
                            }
                        }
                        return true;
                    } else if (!include.isEmpty()) {
                        for (Element element : include) {
                            if (name.equals(element.getTextNormalize())) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                }
            };

            File file = new File(path);
            if (file.exists()) {
                this.worldFiles.addAll(Arrays.asList(file.listFiles(filter)));
            }
        }
    }

    @Override
    public MapContainer loadContainer() {
        MapContainer container = new MapContainer();
        for (File worldDirectory : this.worldFiles) {
            if (!worldDirectory.isDirectory()) {
                continue;
            }

            try {
                OfflineMap map = this.readMapDirectory(worldDirectory);
                if (map != null) {
                    container.register(map);
                }
            } catch (Throwable th) {
                this.getLogger().log(Level.SEVERE, "Could not load map " + worldDirectory.getName() + ": " + th.getMessage(), th);
            }
        }

        return container;
    }

    @Handler(priority = Priority.NORMAL)
    public void onMapContainerFill(MapContainerFillEvent event) {
        event.addMapLoader(this);
    }

    private OfflineMap readMapDirectory(File worldDirectory) throws IOException, MapParserException {
        MapParser.Technology technology = this.getPlugin().getMaps().getParser();
        File file = new File(worldDirectory, technology.getDefaultFilename());
        if (!file.exists()) {
            return null;
        }

        MapParser parser = technology.newInstance();
        parser.readFile(file);

        OfflineMap map = parser.parseOfflineMap();
        map.setDirectory(worldDirectory);
        map.setSettings(file);
        return map;
    }

}
