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

package pl.themolka.arcade.config;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(Ref.class)
public class RefParser extends ElementParser<Ref> {
    @Override
    public Set<Object> expect() {
        return Collections.singleton("an ID reference matching " + Ref.ID_PATTERN.pattern());
    }

    @Override
    protected Result<Ref> parseElement(Element element, String name, String value) throws ParserException {
        if (!this.validId(value)) {
            throw this.fail(element, name, value, "Invalid ID syntax");
        }

        return Result.fine(element, name, value, this.createRef(element, value));
    }

    private Ref<?> createRef(Element element, String id) {
        Ref<?> ref = Ref.of(id);
        if (element.isSelectable()) {
            ref.locate(element.select());
        }

        return ref;
    }

    private boolean validId(String id) throws ParserException {
        return Ref.ID_PATTERN.matcher(id).matches();
    }
}
