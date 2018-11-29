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

package pl.themolka.arcade.filter.matcher;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadePlayer;

public class DamageMatcher extends ConfigurableMatcher<DamageCause> {
    protected DamageMatcher(Config config) {
        super(config.value().get());
    }

    @Override
    public boolean find(Object object) {
        if (object instanceof DamageCause) {
            return this.matches((DamageCause) object);
        } else if (object instanceof ArcadePlayer) {
            return this.matches((ArcadePlayer) object);
        } else if (object instanceof Entity) {
            return this.matches((Entity) object);
        } else if (object instanceof EntityDamageEvent) {
            return this.matches((EntityDamageEvent) object);
        } else if (object instanceof GamePlayer) {
            return this.matches((GamePlayer) object);
        }

        return false;
    }

    public boolean matches(ArcadePlayer player) {
        return player != null && this.matches(player.getGamePlayer());
    }

    public boolean matches(Entity entity) {
        return entity != null && this.matches(entity.getLastDamageCause());
    }

    public boolean matches(EntityDamageEvent event) {
        return event != null && this.matches(event.getCause());
    }

    public boolean matches(GamePlayer player) {
        return player != null && this.matches(player.getBukkit());
    }

    @NestedParserName("damage")
    @Produces(Config.class)
    public static class MatcherParser extends BaseMatcherParser<Config>
                                      implements InstallableParser {
        private Parser<DamageCause> causeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.causeParser = context.type(DamageCause.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            DamageCause cause = this.causeParser.parseWithDefinition(node, name, value).orFail();

            return Result.fine(node, name, value, new Config() {
                public Ref<DamageCause> value() { return Ref.ofProvided(cause); }
            });
        }
    }

    public interface Config extends ConfigurableMatcher.Config<DamageMatcher, DamageCause> {
        @Override
        default DamageMatcher create(Game game, Library library) {
            return new DamageMatcher(this);
        }
    }
}
