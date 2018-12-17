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

package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.IRegionFieldStrategy;
import pl.themolka.arcade.region.UnionRegion;

import java.util.Collections;
import java.util.Set;

@Produces(Capture.Config.class)
public class CaptureParser extends ConfigParser<Capture.Config>
                           implements InstallableParser {
    private Parser<IRegionFieldStrategy> fieldStrategyParser;
    private Parser<Ref> filterParser;
    private Parser<UnionRegion.Config> regionParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.fieldStrategyParser = library.type(IRegionFieldStrategy.class);
        this.filterParser = library.type(Ref.class);
        this.regionParser = library.type(UnionRegion.Config.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("point capture");
    }

    @Override
    protected Result<Capture.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        IRegionFieldStrategy fieldStrategy = this.fieldStrategyParser.parse(context, node.property(
                "field-strategy", "fieldstrategy")).orDefault(Capture.Config.DEFAULT_FIELD_STRATEGY);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(context, node.firstChild("filter")).orDefault(Ref.empty());
        UnionRegion.Config region = this.regionParser.parseWithDefinition(context, node, name, value).orFail();

        return Result.fine(node, name, value, new Capture.Config() {
            public Ref<IRegionFieldStrategy> fieldStrategy() { return Ref.ofProvided(fieldStrategy); }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
        });
    }
}
