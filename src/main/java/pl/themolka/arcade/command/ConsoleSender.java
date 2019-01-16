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

package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.UUID;
import java.util.logging.Level;

public class ConsoleSender implements Sender {
    public static final String CONSOLE_NAME = "Console";
    public static final String CONSOLE_UUID_NAME = "Internal:" + CONSOLE_NAME;
    public static final UUID CONSOLE_UUID = UUID
            .nameUUIDFromBytes(CONSOLE_UUID_NAME.getBytes());

    private final ArcadePlugin plugin;

    public ConsoleSender(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ArcadePlayer getPlayer() {
        throw new UnsupportedOperationException("Not supported by the console");
    }

    @Override
    public ArcadePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getUsername() {
        return getConsoleName();
    }

    @Override
    public UUID getUuid() {
        return CONSOLE_UUID;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public void send(String message) {
        this.plugin.getLogger().log(Level.INFO, ChatColor.stripColor(message));
    }

    @Override
    public void sendAction(String action) {
        this.send("[Action] " + action);
    }

    @Override
    public void sendChat(String chat) {
        this.send("[Chat] " + chat);
    }

    public static String getConsoleName() {
        return "*" + CONSOLE_NAME;
    }

    public static String getConsoleColoredName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() +
                ChatColor.ITALIC + getConsoleName() + ChatColor.RESET;
    }
}
