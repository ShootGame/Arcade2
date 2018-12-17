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

package pl.themolka.arcade.session;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.time.Time;

import java.util.Collections;
import java.util.Set;

@Produces(PlayerTitle.class)
public class PlayerTitleParser extends NodeParser<PlayerTitle>
                               implements InstallableParser {
    private static final BaseComponent empty = new TextComponent();

    private Parser<BaseComponent> primaryParser;
    private Parser<BaseComponent> secondaryParser;
    private Parser<Time> fadeInParser;
    private Parser<Time> viewTimeParser;
    private Parser<Time> fadeOutParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("player title");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.primaryParser = library.type(BaseComponent.class);
        this.secondaryParser = library.type(BaseComponent.class);
        this.fadeInParser = library.type(Time.class);
        this.viewTimeParser = library.type(Time.class);
        this.fadeOutParser = library.type(Time.class);
    }

    @Override
    protected Result<PlayerTitle> parseTree(Context context, Node node, String name) throws ParserException {
        BaseComponent primary = this.primaryParser.parse(context, node.firstChild("title", "primary")).orDefaultNull();
        BaseComponent secondary = this.secondaryParser.parse(context, node.firstChild("subtitle", "sub-title", "secondary")).orDefaultNull();

        if (primary == null && secondary == null) {
            return Result.empty(node, "Missing <title> or <subtitle>");
        }

        PlayerTitle title = new PlayerTitle(primary != null ? primary : empty, secondary != null ? secondary : empty);

        Time fadeIn = this.fadeInParser.parse(context, node.property("fade-in", "fadein")).orDefaultNull();
        if (fadeIn != null) {
            title.setFadeIn(fadeIn);
        }

        Time viewTime = this.viewTimeParser.parse(context, node.property("view-time", "viewtime", "time", "timeout")).orDefaultNull();
        if (viewTime != null) {
            title.setViewTime(viewTime);
        }

        Time fadeOut = this.fadeOutParser.parse(context, node.property("fade-out", "fadeout")).orDefaultNull();
        if (fadeOut != null) {
            title.setFadeOut(fadeOut);
        }

        return Result.fine(node, name, title);
    }
}
