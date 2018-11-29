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

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.game.Game;

public class TeamBuilder implements Builder<Team> {
    private final Game game;

    private final String id;
    private ChatColor color;
    private DyeColor dyeColor;
    private boolean friendlyFire;
    private int maxPlayers;
    private int minPlayers;
    private String name;
    private int slots;

    public TeamBuilder(Game game, String id) {
        this.game = game;
        this.id = id;
    }

    @Override
    public Team build() {
        Team team = new Team(this.game, this.id());
        team.setChatColor(this.color());
        team.setDyeColor(this.dyeColor());
        team.setFriendlyFire(this.friendlyFire());
        team.setMaxPlayers(this.maxPlayers());
        team.setMinPlayers(this.minPlayers());
        team.setName(this.name());
        team.setSlots(this.slots());

        return team;
    }

    public ChatColor color() {
        return this.color;
    }

    public TeamBuilder color(ChatColor color) {
        this.color = color;
        return this;
    }

    public DyeColor dyeColor() {
        return this.dyeColor;
    }

    public TeamBuilder dyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
        return this;
    }

    public boolean friendlyFire() {
        return this.friendlyFire;
    }

    public TeamBuilder friendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
        return this;
    }

    public String id() {
        return this.id;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    public TeamBuilder maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public int minPlayers() {
        return this.minPlayers;
    }

    public TeamBuilder minPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }

    public String name() {
        return this.name;
    }

    public TeamBuilder name(String name) {
        this.name = name;
        return this;
    }

    public int slots() {
        return this.slots;
    }

    public TeamBuilder slots(int slots) {
        this.slots = slots;
        return this;
    }
}
