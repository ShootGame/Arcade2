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

import java.util.ArrayList;
import java.util.List;

public class Preprocessor implements Preprocess {
    private final List<Preprocess> executors = new ArrayList<>();

    @Override
    public void process(Document document) throws PreprocessException {
        for (Preprocess executor : this.executors) {
            try {
                executor.process(document);
            } catch (PreprocessNotSupportedException ignored) {
            }
        }
    }

    @Override
    public void process(Node node) throws PreprocessException {
        for (Preprocess executor : this.executors) {
            try {
                executor.process(node);
            } catch (PreprocessNotSupportedException ignored) {
            }
        }
    }

    @Override
    public void process(Property property) throws PreprocessException {
        for (Preprocess executor : this.executors) {
            try {
                executor.process(property);
            } catch (PreprocessNotSupportedException ignored) {
            }
        }
    }

    public List<Preprocess> getExecutors() {
        return new ArrayList<>(this.executors);
    }

    public boolean install(Preprocess executor) {
        return this.executors.add(executor);
    }
}
