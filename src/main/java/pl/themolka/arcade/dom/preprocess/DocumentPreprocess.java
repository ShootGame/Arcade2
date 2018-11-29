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
import pl.themolka.arcade.dom.Property;

import java.util.Collections;
import java.util.List;

public abstract class DocumentPreprocess implements DefinedPreprocess<Document, Document>, Preprocess {
    @Override
    public List<Document> define(Document document) {
        return Collections.singletonList(document);
    }

    @Override
    public void preprocess(Document document) throws PreprocessException {
        this.invoke(document);
    }

    @Override
    public final void preprocess(Node node) throws PreprocessException {
        throw new PreprocessNotSupportedException(node);
    }

    @Override
    public final void preprocess(Property property) throws PreprocessException {
        throw new PreprocessNotSupportedException(property);
    }
}
