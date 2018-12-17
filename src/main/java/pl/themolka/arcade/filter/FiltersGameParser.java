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

package pl.themolka.arcade.filter;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(FiltersGame.Config.class)
public class FiltersGameParser extends GameModuleParser<FiltersGame, FiltersGame.Config>
                               implements InstallableParser {
    private Parser<FilterSet.Config> filterSetParser;

    public FiltersGameParser() {
        super(FiltersGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("filters", "filter");
    }

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.filterSetParser = library.type(FilterSet.Config.class);
    }

    @Override
    protected Result<FiltersGame.Config> parseTree(Context context, Node node, String name) throws ParserException {
        Set<FilterSet.Config> filterSets = new LinkedHashSet<>();
        for (Node filterSetNode : node.children("filter")) {
            filterSets.add(this.filterSetParser.parse(context, filterSetNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(filterSets)) {
            throw this.fail(node, name, null, "No filter groups defined");
        }

        return Result.fine(node, name, null, new FiltersGame.Config() {
            public Ref<Set<FilterSet.Config>> filterSets() { return Ref.ofProvided(filterSets); }
        });
    }
}
