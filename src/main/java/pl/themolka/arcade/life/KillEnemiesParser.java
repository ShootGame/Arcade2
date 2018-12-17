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
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
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
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.enemyParser = library.type(Ref.class);
        this.ownerParser = library.type(Ref.class);
        this.nameParser = library.type(String.class);
    }

    @Override
    protected Result<KillEnemies.Config> parsePrimitive(Context context, Node node, String name, String value) throws ParserException {
        String id = this.parseId(context, node);
        Set<Ref<Participator.Config<?>>> enemies = Collections.singleton(this.enemyParser.parse(context, node).orFail());
        Ref<Participator.Config<?>> owner = this.parseOwner(context, node);
        String goalName = this.parseName(context, node);

        return Result.fine(node, name, value, new KillEnemies.Config() {
            public String id() { return id; }
            public Ref<Set<Ref<Participator.Config<?>>>> enemies() { return Ref.ofProvided(enemies); }
            public Ref<Participator.Config<?>> owner() { return owner; }
            public Ref<String> name() { return Ref.ofProvided(goalName); }
        });
    }

    @Override
    protected Result<KillEnemies.Config> parseTree(Context context, Node node, String name) throws ParserException {
        String id = this.parseId(context, node);
        Set<Ref<Participator.Config<?>>> enemies = this.parseEnemies(context, node, name, null);
        Ref<Participator.Config<?>> owner = this.parseOwner(context, node);
        String goalName = this.parseName(context, node);

        return Result.fine(node, name, new KillEnemies.Config() {
            public String id() { return id; }
            public Ref<Set<Ref<Participator.Config<?>>>> enemies() { return Ref.ofProvided(enemies); }
            public Ref<Participator.Config<?>> owner() { return owner; }
            public Ref<String> name() { return Ref.ofProvided(goalName); }
        });
    }

    protected String parseId(Context context, Node node) throws ParserException {
        return this.parseOptionalId(context, node);
    }

    protected Set<Ref<Participator.Config<?>>> parseEnemies(Context context, Node node, String name, String value) throws ParserException {
        Set<Ref<Participator.Config<?>>> enemies = new LinkedHashSet<>();
        for (Node enemyNode : node.children("enemy", "kill")) {
            enemies.add(this.enemyParser.parse(context, enemyNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(enemies)) {
            throw this.fail(node, name, value, "No enemies defined");
        }

        return enemies;
    }

    protected Ref<Participator.Config<?>> parseOwner(Context context, Node node) throws ParserException {
        return this.ownerParser.parse(context, node.property("owner", "of")).orFail();
    }

    protected String parseName(Context context, Node node) throws ParserException {
        return this.nameParser.parse(context, node.property("name", "title")).orDefaultNull();
    }
}
