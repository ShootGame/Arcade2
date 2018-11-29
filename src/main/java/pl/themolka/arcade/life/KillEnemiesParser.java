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

package pl.themolka.arcade.life;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Produces(KillEnemies.Config.class)
public class KillEnemiesParser extends ConfigParser<KillEnemies.Config>
                               implements InstallableParser {
    private Parser<Ref> enemyParser;
    private Parser<Ref> ownerParser;
    private Parser<String> nameParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("kill enemies objective");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.enemyParser = context.type(Ref.class);
        this.ownerParser = context.type(Ref.class);
        this.nameParser = context.type(String.class);
    }

    @Override
    protected Result<KillEnemies.Config> parsePrimitive(Node node, String name, String value) throws ParserException {
        String id = this.parseId(node);
        Set<Ref<Participator.Config<?>>> enemies = Collections.singleton(this.enemyParser.parse(node).orFail());
        Ref<Participator.Config<?>> owner = this.parseOwner(node);
        String goalName = this.parseName(node);

        return Result.fine(node, name, value, new KillEnemies.Config() {
            public String id() { return id; }
            public Ref<Set<Ref<Participator.Config<?>>>> enemies() { return Ref.ofProvided(enemies); }
            public Ref<Participator.Config<?>> owner() { return owner; }
            public Ref<String> name() { return Ref.ofProvided(goalName); }
        });
    }

    @Override
    protected Result<KillEnemies.Config> parseTree(Node node, String name) throws ParserException {
        String id = this.parseId(node);
        Set<Ref<Participator.Config<?>>> enemies = this.parseEnemies(node, name, null);
        Ref<Participator.Config<?>> owner = this.parseOwner(node);
        String goalName = this.parseName(node);

        return Result.fine(node, name, new KillEnemies.Config() {
            public String id() { return id; }
            public Ref<Set<Ref<Participator.Config<?>>>> enemies() { return Ref.ofProvided(enemies); }
            public Ref<Participator.Config<?>> owner() { return owner; }
            public Ref<String> name() { return Ref.ofProvided(goalName); }
        });
    }

    protected String parseId(Node node) throws ParserException {
        return this.parseOptionalId(node);
    }

    protected Set<Ref<Participator.Config<?>>> parseEnemies(Node node, String name, String value) throws ParserException {
        Set<Ref<Participator.Config<?>>> enemies = new LinkedHashSet<>();
        for (Node enemyNode : node.children("enemy", "kill")) {
            enemies.add(this.enemyParser.parse(enemyNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(enemies)) {
            throw this.fail(node, name, value, "No enemies defined");
        }

        return enemies;
    }

    protected Ref<Participator.Config<?>> parseOwner(Node node) throws ParserException {
        return this.ownerParser.parse(node.property("owner", "of")).orFail();
    }

    protected String parseName(Node node) throws ParserException {
        return this.nameParser.parse(node.property("name", "title")).orDefaultNull();
    }
}
