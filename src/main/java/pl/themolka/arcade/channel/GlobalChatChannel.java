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

package pl.themolka.arcade.channel;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Sender;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatChannel extends ChatChannel {
    public static final String GLOBAL_CHANNEL_ID = "_global";
    public static final String GLOBAL_CHANNEL_KEY = "!";
    public static final String GLOBAL_FORMAT = ChatColor.DARK_GRAY + "[" +
            ChatColor.AQUA + ChatColor.BOLD + "G" + ChatColor.RESET +
            ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW + "%s" +
            ChatColor.DARK_GRAY + ": " +  ChatColor.WHITE + "%s";
    public static final String GLOBAL_PERMISSION_NODE =
            getPermissionNode("global");

    private final ArcadePlugin plugin;

    private String format;

    public GlobalChatChannel(ArcadePlugin plugin, String format) {
        super(plugin, GLOBAL_CHANNEL_ID);
        this.plugin = plugin;

        this.format = format;
    }

    @Override
    public boolean addMember(Sender member) {
        return false;
    }

    @Override
    public String getFormat() {
        return this.format;
    }

    @Override
    public List<Sender> getMembers() {
        return new ArrayList<>(this.plugin.getPlayers());
    }

    @Override
    public String getPermission() {
        return GLOBAL_PERMISSION_NODE;
    }

    @Override
    public boolean hasMember(Sender member) {
        return !member.isConsole();
    }

    @Override
    public boolean removeMember(Sender member) {
        return false;
    }

    @Override
    public void setFormat(String format) {
        throw new UnsupportedOperationException("Not supported here.");
    }
}
