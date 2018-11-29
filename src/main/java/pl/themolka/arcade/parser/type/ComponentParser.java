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

package pl.themolka.arcade.parser.type;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(BaseComponent.class)
public class ComponentParser extends ElementParser<BaseComponent>
                             implements InstallableParser {
    private Parser<String> messageParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("chat component");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.messageParser = context.type(String.class);
    }

    @Override
    protected Result<BaseComponent> parseElement(Element element, String name, String value) throws ParserException {
        BaseComponent[] text = TextComponent.fromLegacyText(this.messageParser.parse(element).orFail());
        return Result.fine(element, name, value, new TextComponent(text));
    }
}
