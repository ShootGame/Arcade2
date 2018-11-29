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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

/**
 * Something that can read DOM content and convert it into a {@link Document}.
 */
public interface DOMEngine {
    default Document read(File file) throws DOMException, IOException {
        return this.read(file.getAbsoluteFile().toURI());
    }

    default Document read(InputStream stream) throws DOMException, IOException {
        try (Reader reader = new InputStreamReader(stream)) {
            return this.read(reader);
        }
    }

    Document read(Reader reader) throws DOMException, IOException;

    default Document read(String dom) throws DOMException, IOException {
        try (Reader reader = new StringReader(dom)) {
            return this.read(reader);
        }
    }

    default Document read(URI uri) throws DOMException, IOException {
        try (InputStream stream = uri.toURL().openStream()) {
            return this.read(stream);
        }
    }
}
