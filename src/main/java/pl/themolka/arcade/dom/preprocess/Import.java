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
import pl.themolka.arcade.dom.engine.EngineManager;

/**
 * Include documents from its own path.
 */
public class Import extends DocumentPreprocess {
    private final EngineManager engines;
    private final Preprocessor preprocessor;

    public Import(EngineManager engines, Preprocessor preprocessor) {
        this.engines = engines;
        this.preprocessor = preprocessor;
    }

    @Override
    public void invoke(Document document) throws PreprocessException {
        if (!document.hasPath()) {
            throw new PreprocessException(document, "Cannot resolve document path");
        }

        ImportStage stage = new ImportStage(this.engines, this.preprocessor, document);
        Preprocess preprocess = new TreePreprocess(stage);
        preprocess.process(document);
    }
}
