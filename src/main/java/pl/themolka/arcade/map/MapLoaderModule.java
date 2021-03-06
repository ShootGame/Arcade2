/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.map;

import net.engio.mbassy.listener.Handler;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.engine.DOMEngine;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.SimpleGlobalModule;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@ModuleInfo(id = "Map-Loader")
public class MapLoaderModule extends SimpleGlobalModule implements MapContainerLoader {
    private final List<File> worldFiles = new ArrayList<>();

    @Override
    public void onEnable(Node options) throws DOMException {
        for (Node directory : options.children("directory")) {
            String path = directory.propertyValue("path");
            List<Node> exclude = directory.children("exclude");
            List<Node> include = directory.children("include");

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!exclude.isEmpty()) {
                        for (Node node : exclude) {
                            if (name.equals(node.getValue())) {
                                return false;
                            }
                        }
                        return true;
                    } else if (!include.isEmpty()) {
                        for (Node node : include) {
                            if (name.equals(node.getValue())) {
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
            } catch (DOMException ex) {
                this.getLogger().log(Level.SEVERE, "Could not load map '" + worldDirectory.getName() + "': " + ex.toString());
            } catch (Throwable th) {
                String message = th.getMessage();
                if (message == null) {
                    message = th.getClass().getName();
                }

                this.getLogger().log(Level.SEVERE, "Could not load map '" + worldDirectory.getName() + "': " + message);
            }
        }

        return container;
    }

    @Handler(priority = Priority.NORMAL)
    public void onMapContainerFill(MapContainerFillEvent event) {
        event.addMapLoader(this);
    }

    private OfflineMap readMapDirectory(File worldDirectory) throws DOMException, IOException {
        File file = new File(worldDirectory, MapManifest.FILENAME);
        if (!file.exists()) {
            throw new FileNotFoundException("Missing " + file.getName());
        }

        DOMEngine engine = this.getPlugin().getDomEngines().forFile(file);

        Parser<OfflineMap> parser;
        try {
            parser = this.getPlugin().getParsers().forType(OfflineMap.class);
        } catch (ParserNotSupportedException ex) {
            throw new RuntimeException("No " + OfflineMap.class.getSimpleName() + " parser installed");
        }

        Document document = engine.read(file);
        this.getPlugin().getDomPreprocessor().preprocess(document);

        Context context = new Context(this.getPlugin());

        OfflineMap map = parser.parse(context, document).orFail();
        map.setDirectory(worldDirectory);
        map.setSettings(file);
        return map;
    }

}
