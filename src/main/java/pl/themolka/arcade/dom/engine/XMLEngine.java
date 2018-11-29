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

import org.xml.sax.InputSource;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Base interface for all XML-oriented engines.
 */
public interface XMLEngine extends DOMEngine {
    @Override
    default Document read(InputStream stream) throws DOMException, IOException {
        return this.read(new InputSource(stream));
    }

    @Override
    default Document read(Reader reader) throws DOMException, IOException {
        return this.read(new InputSource(reader));
    }

    Document read(InputSource source) throws DOMException, IOException;
}
