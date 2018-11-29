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

package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;

public class BaseRemovableContentParser<T extends RemovableKitContent.Config<? extends RemovableKitContent<?>, ?>>
        extends BaseContentParser<T> implements InstallableParser {
    private Parser<Boolean> resetParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.resetParser = context.type(Boolean.class);
    }

    protected boolean reset(Node node) throws ParserException {
        return this.resetParser.parse(node.property("reset")).orDefault(false);
    }
}
