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

package pl.themolka.arcade.module;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModulesFile {
    public static final String DEFAULT_FILENAME = "modules.xml";

    private final Document document;

    public ModulesFile(ArcadePlugin plugin, InputStream input) throws DOMException, IOException {
        this.document = plugin.getDomEngines().forFile(DEFAULT_FILENAME).read(input);
    }

    public Document getDocument() {
        return this.document;
    }

    public List<Module<?>> getModules() {
        return this.getModules(this.getDocument());
    }

    public List<Module<?>> getModules(Document document) {
        return this.getModules(document.getRoot());
    }

    public List<Module<?>> getModules(Node parent) {
        List<Module<?>> modules = new ArrayList<>();

        for (Node node : parent.children("module")) {
            try {
                Module<?> module = (Module<?>) Class.forName(node.propertyValue("class")).newInstance();
                if (module != null) {
                    modules.add(module);
                }
            } catch (ClassCastException cast) {
                throw new RuntimeException("module class does not inherit the " + Module.class.getName());
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

        return modules;
    }
}
