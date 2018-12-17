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

package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

import java.util.Collections;
import java.util.Set;

/**
 * This is the default parser behavior. We are using {@link ElementParser} for
 * it, so the {@code value} parameter is never {@code null}.
 */
public class TextParser extends ElementParser<String> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("a text");
    }

    @Override
    protected Result<String> parseElement(Context context, Element element, String name, String value) throws ParserException {
        return Result.fine(element, name, value, value);
    }
}
