package pl.themolka.arcade.map;

import net.engio.mbassy.listener.Handler;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.SimpleGlobalModule;
import pl.themolka.arcade.repository.RepositoriesModule;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@ModuleInfo(id = "Map-Loader",
        loadBefore = {
                RepositoriesModule.class})
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
                            if (name.equals(element.getValue())) {
                                return false;
                            }
                        }
                        return true;
                    } else if (!include.isEmpty()) {
                        for (Element element : include) {
                            if (name.equals(element.getValue())) {
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
                for (File worldFile : file.listFiles(filter)) {
                    if (!this.worldFiles.contains(worldFile)) {
                        this.worldFiles.add(worldFile);
                    }
                }
            }
        }
    }

    @Override
    public MapContainer loadContainer() {
        MapContainer container = new MapContainer();
        List<String> registeredNames = new ArrayList<>();

        for (File worldDirectory : this.worldFiles) {
            if (!worldDirectory.isDirectory()) {
                continue;
            }

            try {
                OfflineMap map = this.readMapDirectory(worldDirectory);
                if (map != null) {
                    if (registeredNames.contains(map.getName())) {
                        this.getLogger().log(Level.CONFIG, "'" + map.getName() + "' from '" + worldDirectory.getPath() + "' is a duplicate.");
                        continue;
                    }

                    container.register(map);
                    registeredNames.add(map.getName());
                }
            } catch (Throwable th) {
                String message = th.getMessage();
                if (message == null) {
                    message = th.getClass().getName();
                }

                this.getLogger().log(Level.SEVERE, "Could not load map " + worldDirectory.getName() + ": " + message);
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

        try {
            OfflineMap map = parser.parseOfflineMap(this.getPlugin());
            map.setDirectory(worldDirectory);
            map.setSettings(file);
            return map;
        } catch (MapParserException ex) {
            this.getLogger().log(Level.CONFIG, "Could not load '" + file.getName() + "': " + ex.getMessage());
            return null;
        }
    }

}
