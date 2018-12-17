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

package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.util.Color;

import java.util.Collections;
import java.util.Set;

@Produces(Team.Config.class)
public class TeamParser extends ConfigParser<Team.Config>
                        implements InstallableParser {
    private Parser<ChatColor> chatColorParser;
    private Parser<DyeColor> dyeColorParser;
    private Parser<Boolean> friendlyFireParser;
    private Parser<Integer> minPlayers;
    private Parser<Integer> maxPlayers;
    private Parser<String> nameParser;
    private Parser<Integer> slotsParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        super.install(library);
        this.chatColorParser = library.type(ChatColor.class);
        this.dyeColorParser = library.type(DyeColor.class);
        this.friendlyFireParser = library.type(Boolean.class);
        this.minPlayers = library.type(Integer.class);
        this.maxPlayers = library.type(Integer.class);
        this.nameParser = library.type(String.class);
        this.slotsParser = library.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("team");
    }

    @Override
    protected Result<Team.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
        String id = this.parseRequiredId(context, node);
        ChatColor chatColor = this.chatColorParser.parse(context, node.property("color", "chat-color", "chatcolor", "chat")).orFail();
        DyeColor dyeColor = this.parseDyeColor(context, chatColor, node.property("dye-color", "dyecolor", "dye"));
        boolean friendlyFire = this.friendlyFireParser.parse(context, node.property("friendly-fire", "friendlyfire", "friendly"))
                .orDefault(Team.Config.DEFAULT_IS_FRIENDLY_FIRE);
        int minPlayers = this.parseMinPlayers(context, node.property("min-players", "minplayers", "min"));
        int maxPlayers = this.parseMaxPlayers(context, node.property("max-players", "maxplayers", "max", "overfill"));
        String teamName = this.nameParser.parse(context, node.property("name", "title")).orFail();
        int slots = this.slotsParser.parse(context, node.property("slots")).orFail();

        return Result.fine(node, name, value, new Team.Config() {
            public String id() { return id; }
            public Ref<ChatColor> chatColor() { return Ref.ofProvided(chatColor); }
            public Ref<DyeColor> dyeColor() { return Ref.ofProvided(dyeColor); }
            public Ref<Boolean> friendlyFire() { return Ref.ofProvided(friendlyFire); }
            public Ref<Integer> minPlayers() { return Ref.ofProvided(minPlayers); }
            public Ref<Integer> maxPlayers() { return Ref.ofProvided(maxPlayers); }
            public Ref<String> name() { return Ref.ofProvided(teamName); }
            public Ref<Integer> slots() { return Ref.ofProvided(slots); }
        });
    }

    protected DyeColor parseDyeColor(Context context, ChatColor chatColor, Property property) throws ParserException {
        DyeColor dyeColor = this.dyeColorParser.parse(context, property).orDefaultNull();
        if (dyeColor != null) {
            return dyeColor;
        }

        Color localColor = Color.ofChat(chatColor);
        if (localColor == null) {
            throw this.fail(property, property.getName(), property.getValue(), "Unknown chat color type");
        }

        dyeColor = localColor.toDye();
        if (dyeColor == null) {
            throw this.fail(property, property.getName(), property.getValue(), "Given chat color type cannot be converted into a dye color");
        }

        return dyeColor;
    }

    protected int parseMinPlayers(Context context, Property property) throws ParserException {
        int minPlayers = this.minPlayers.parse(context, property).orDefault(Team.Config.DEFAULT_MIN_PLAYERS);
        if (minPlayers < 0) {
            throw this.fail(property, property.getName(), property.getValue(), "Minimal player count cannot be negative (smaller than 0)");
        }

        return minPlayers;
    }

    protected int parseMaxPlayers(Context context, Property property) throws ParserException {
        int maxPlayers = this.maxPlayers.parse(context, property).orDefault(Team.Config.DEFAULT_MAX_PLAYERS);
        if (maxPlayers < 0) {
            throw this.fail(property, property.getName(), property.getValue(), "Maximal player count cannot be negative (smaller than 0)");
        }

        return maxPlayers;
    }
}
