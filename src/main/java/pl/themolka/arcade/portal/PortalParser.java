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
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.RegionParser;
import pl.themolka.arcade.region.UnionRegion;
import pl.themolka.arcade.spawn.Direction;
import pl.themolka.arcade.spawn.SmoothSpawnAgent;
import pl.themolka.arcade.spawn.Spawn;
import pl.themolka.arcade.spawn.SpawnAgent;
import pl.themolka.arcade.spawn.SpawnApply;

import java.util.Collections;
import java.util.Set;

@Produces(Portal.Config.class)
public class PortalParser extends ConfigParser<Portal.Config>
                          implements InstallableParser {
    private Parser<Ref> destinationParser;
    private Parser<Direction> yawDirectionParser;
    private Parser<Direction> pitchDirectionParser;
    private Parser<Ref> filterParser;
    private Parser<Ref> kitParser;
    private Parser<UnionRegion.Config> regionParser;
    private Parser<Boolean> smoothParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.destinationParser = context.type(Ref.class);
        this.yawDirectionParser = context.type(Direction.class);
        this.pitchDirectionParser = context.type(Direction.class);
        this.filterParser = context.type(Ref.class);
        this.kitParser = context.type(Ref.class);
        this.regionParser = context.of(RegionParser.Union.class);
        this.smoothParser = context.type(Boolean.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("portal");
    }

    @Override
    protected Result<Portal.Config> parseNode(Node node, String name, String value) throws ParserException {
        SpawnApply.Config destination = this.parseDestination(node, name, value);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());
        String id = this.parseOptionalId(node);
        Ref<Kit.Config> kit = this.kitParser.parse(node.property("kit")).orDefault(Ref.empty());
        AbstractRegion.Config<?> region = this.regionParser.parseWithDefinition(node, name, value).orFail();

        return Result.fine(node, name, value, new Portal.Config() {
            public Ref<SpawnApply.Config> destination() { return Ref.ofProvided(destination); }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public String id() { return id; }
            public Ref<Kit.Config> kit() { return kit; }
            public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
        });
    }

    protected SpawnApply.Config parseDestination(Node node, String name, String value) throws ParserException {
        Ref<Spawn.Config<?>> destination = this.destinationParser.parse(node.property("destination")).orFail();
        boolean smooth = this.smoothParser.parse(node.property("smooth")).orDefault(false);

        Direction yaw = this.yawDirectionParser.parse(node.property("yaw")).orDefault(Direction.ENTITY);
        Direction pitch = this.pitchDirectionParser.parse(node.property("pitch")).orDefault(Direction.ENTITY);

        return new SpawnApply.Config() {
            public Ref<Spawn.Config<?>> spawn() { return destination; }
            public Ref<SpawnApply.AgentFactory> agentFactory() {
                return Ref.ofProvided(new SpawnApply.AgentFactory() {
                    @Override
                    public SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit) {
                        if (smooth) {
                            return SmoothSpawnAgent.create(spawn, player.getBukkit(), yaw, pitch);
                        } else {
                            return SpawnAgent.create(spawn, player.getBukkit(), yaw, pitch);
                        }
                    }
                });
            }
        };
    }
}
