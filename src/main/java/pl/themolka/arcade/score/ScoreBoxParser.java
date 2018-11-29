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

package pl.themolka.arcade.score;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.portal.Portal;
import pl.themolka.arcade.portal.PortalParser;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.spawn.SpawnApply;

@Produces(ScoreBox.Config.class)
public class ScoreBoxParser extends PortalParser
                            implements InstallableParser {
    private Parser<Double> pointsParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.pointsParser = context.type(Double.class);
    }

    @Override
    protected Result<Portal.Config> parseNode(Node node, String name, String value) throws ParserException {
        Portal.Config portal = super.parseNode(node, name, value).orFail();
        double points = this.pointsParser.parse(node.property("points")).orFail();

        return Result.fine(node, name, value, new ScoreBox.Config() {
            public Ref<SpawnApply.Config> destination() { return portal.destination(); }
            public Ref<Filter.Config<?>> filter() { return portal.filter(); }
            public String id() { return portal.id(); }
            public Ref<Kit.Config> kit() { return portal.kit(); }
            public Ref<Double> points() { return Ref.ofProvided(points); }
            public Ref<AbstractRegion.Config<?>> region() { return portal.region(); }
        });
    }
}
