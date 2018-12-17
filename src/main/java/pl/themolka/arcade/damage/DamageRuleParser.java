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

package pl.themolka.arcade.damage;

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
import pl.themolka.arcade.parser.type.PercentageParser;
import pl.themolka.arcade.util.Percentage;

import java.util.Collections;
import java.util.Set;

@Produces(DamageRule.Config.class)
public class DamageRuleParser extends ConfigParser<DamageRule.Config>
                              implements InstallableParser {
    private Parser<Ref> entityFilterParser;
    private Parser<Ref> playerFilterParser;
    private Parser<Boolean> denyParser;
    private Parser<Double> damageParser;
    private Parser<Percentage> multiplierParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.entityFilterParser = library.type(Ref.class);
        this.playerFilterParser = library.type(Ref.class);
        this.denyParser = library.type(Boolean.class);
        this.damageParser = library.type(Double.class);
        this.multiplierParser = library.of(PercentageParser.Infinite.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("damage rule");
    }

    @Override
    protected Result<DamageRule.Config> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        String id = this.parseOptionalId(context, node);
        Ref<Filter.Config<?>> entityFilter = this.entityFilterParser.parse(context, node.property("entity-filter", "filter")).orDefault(Ref.empty());
        Ref<Filter.Config<?>> playerFilter = this.playerFilterParser.parse(context, node.property("player-filter", "filter")).orDefault(Ref.empty());

        boolean notDenied = this.denyParser.parse(context, node).orDefault(true);
        double damage = notDenied ? this.damageParser.parse(context, node).orFail()
                                  : DamageRule.Config.DENY_DAMAGE;
        Percentage multiplier = this.multiplierParser.parse(context, node.property("multiplier", "multiply")).orDefault(Percentage.DONE);

        return Result.fine(node, name, value, new DamageRule.Config() {
            public String id() { return id; }
            public Ref<Double> damage() { return Ref.ofProvided(damage); }
            public Ref<Filter.Config<?>> entityFilter() { return entityFilter; }
            public Ref<Percentage> multiplier() { return Ref.ofProvided(multiplier); }
            public Ref<Filter.Config<?>> playerFilter() { return playerFilter; }
        });
    }
}
