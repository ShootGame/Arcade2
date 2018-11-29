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
import pl.themolka.arcade.command.ConsoleSender;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class ChatChannel extends AbstractChannel {
    public static final String EMPTY_MESSAGE = "No message specified.";
    public static final String PERMISSION_ERROR =
            "You don't have permission to write to this channel.";
    public static final String PERMISSION_NODE = "arcade.channel";

    private final ArcadePlugin plugin;

    private String format;
    private final List<Sender> members = new ArrayList<>();

    public ChatChannel(ArcadePlugin plugin, String id) {
        super(id);
        this.plugin = plugin;
    }

    @Override
    public boolean addMember(Sender member) {
        return this.members.add(member);
    }

    @Override
    public List<Sender> getMembers() {
        return this.members;
    }

    @Override
    public String getPermission() {
        return getPermissionNode(super.getPermission());
    }

    @Override
    public boolean hasMember(Sender member) {
        return this.members.contains(member);
    }

    @Override
    public boolean removeMember(Sender member) {
        return this.members.remove(member);
    }

    @Override
    public int sendChatMessage(Sender author, String message) {
        int empty = 0;
        if (this.getPermission() != null &&
                !author.hasPermission(this.getPermission())) {
            author.sendError(PERMISSION_ERROR);
            return empty;
        } else if (message.isEmpty()) {
            author.sendError(EMPTY_MESSAGE);
            return empty;
        }

        String name;
        if (author.isConsole()) {
            name = ConsoleSender.getConsoleColoredName();
        } else {
            GamePlayer player = author.getGamePlayer();
            name = player.hasDisplayName() ? player.getFullName() : player.getUsername();
        }

        // event
        ChannelMessageEvent event = new ChannelMessageEvent(
                this.plugin, this, author, name, message);
        this.plugin.getEventBus().publish(event);

        if (!event.isCanceled()) {
            return this.sendMessage(event.getAuthorName(), event.getMessage());
        }

        return empty;
    }

    public String formatMessage(String author, String message) {
        return String.format(this.getFormat(), author, message);
    }

    public String getFormat() {
        return this.format;
    }

    public int sendMessage(String author, String message) {
        this.plugin.getLogger().info(String.format("[Chat '%s'] %s: %s",
                this.getId(), ChatColor.stripColor(author), message));
        return this.sendChatMessage(this.formatMessage(author, message));
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public static String getPermissionNode(String node) {
        return PERMISSION_NODE + "." + node;
    }
}
