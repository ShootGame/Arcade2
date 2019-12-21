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

package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Propertable;
import pl.themolka.arcade.dom.Property;

import java.util.List;

public abstract class PropertyPreprocess implements DefinedPreprocess<Property, Propertable>, Preprocess {
    @Override
    public List<Property> define(Propertable propertable) {
        return propertable.properties();
    }

    @Override
    public final void process(Document document) throws PreprocessException {
        if (document.hasRoot()) {
            this.process(document.getRoot());
        }
    }

    @Override
    public final void process(Node node) throws PreprocessException {
        List<Property> definition = this.define(node);
        if (definition != null) {
            for (Property property : definition) {
                this.process(property);
            }
        }
    }

    @Override
    public final void process(Property property) throws PreprocessException {
        this.invoke(property);
    }
}
