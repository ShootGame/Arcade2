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

package pl.themolka.arcade.dom.engine;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Namespace;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Converting .properties files into DOM objects. All properties will be stored
 * as {@link Node}s under the root {@link Node}.
 */
@FileExtensions("properties")
public class PropertiesEngine implements DOMEngine {
    public static final Namespace NAMESPACE = Namespace.getDefault();

    @Override
    public Document read(InputStream stream) throws DOMException, IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return this.read(null, properties);
    }

    @Override
    public Document read(Reader reader) throws DOMException, IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return this.read(null, properties);
    }

    private Document read(Path path, Properties properties) {
        return Document.create(path, this.convert(properties));
    }

    private Node convert(Properties properties) {
        List<Property> values = new ArrayList<>();
        for (String name : properties.stringPropertyNames()) {
            values.add(Property.of(NAMESPACE, name, properties.getProperty(name)));
        }

        return Node.of(NAMESPACE, Properties.class.getSimpleName(), values);
    }
}
