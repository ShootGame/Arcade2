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

package pl.themolka.arcade.portal;

import org.bukkit.entity.Player;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.RegionParser;
import pl.themolka.arcade.region.UnionRegion;
import pl.themolka.arcade.spawn.DirectionTranslator;
import pl.themolka.arcade.spawn.Spawn;
import pl.themolka.arcade.spawn.SpawnAgent;
import pl.themolka.arcade.spawn.SpawnApply;

import java.util.Collections;
import java.util.Set;

@Produces(Portal.Config.class)
public class PortalParser extends ConfigParser<Portal.Config>
                          implements InstallableParser {
    private Parser<Ref> destinationParser;
    private Parser<DirectionTranslator> yawDirectionParser;
    private Parser<DirectionTranslator> pitchDirectionParser;
    private Parser<Ref> filterParser;
    private Parser<Ref> kitParser;
    private Parser<UnionRegion.Config> regionParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.destinationParser = library.type(Ref.class);
        this.yawDirectionParser = library.type(DirectionTranslator.class);
        this.pitchDirectionParser = library.type(DirectionTranslator.class);
        this.filterParser = library.type(Ref.class);
        this.kitParser = library.type(Ref.class);
        this.regionParser = library.of(RegionParser.Union.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("portal");
    }

    @Override
    protected Result<Portal.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        SpawnApply.Config destination = this.parseDestination(context, node, name, value);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(context, node.property("filter")).orDefault(Ref.empty());
        String id = this.parseOptionalId(context, node);
        Ref<Kit.Config> kit = this.kitParser.parse(context, node.property("kit")).orDefault(Ref.empty());
        AbstractRegion.Config<?> region = this.regionParser.parseWithDefinition(context, node, name, value).orFail();

        return Result.fine(node, name, value, new Portal.Config() {
            public Ref<SpawnApply.Config> destination() { return Ref.ofProvided(destination); }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public String id() { return id; }
            public Ref<Kit.Config> kit() { return kit; }
            public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
        });
    }

    protected SpawnApply.Config parseDestination(Context context, Node node, String name, String value) throws ParserException {
        Ref<Spawn.Config<?>> destination = this.destinationParser.parse(context, node.property("destination")).orFail();

        DirectionTranslator yaw = this.yawDirectionParser.parse(context, node.property("yaw")).orDefault(DirectionTranslator.ENTITY);
        DirectionTranslator pitch = this.pitchDirectionParser.parse(context, node.property("pitch")).orDefault(DirectionTranslator.ENTITY);

        SpawnApply.AgentFactory agentFactory = new SpawnApply.AgentFactory() {
            @Override
            public SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit) {
                return SpawnAgent.create(spawn, player.getBukkit(), yaw, pitch);
            }
        };

        return new SpawnApply.Config() {
            public Ref<Spawn.Config<?>> spawn() { return destination; }
            public Ref<SpawnApply.AgentFactory> agentFactory() { return Ref.ofProvided(agentFactory); }
        };
    }
}
