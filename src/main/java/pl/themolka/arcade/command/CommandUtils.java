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

public final class CommandUtils {
    public static final int CHAT_LINE_LENGTH = 50;

    private CommandUtils() {
    }

    public static String createLine(int length) {
        return createLine(length, ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-");
    }

    public static String createLine(int length, String type) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append(type);
        }

        return line.toString();
    }

    public static String createTitle(String title) {
        return createTitle(title, null);
    }

    public static String createTitle(String title, String extra) {
        String head = ChatColor.GOLD + title + ChatColor.RESET;
        if (extra != null) {
            head += ChatColor.GRAY + " (" + extra + ")";
        }

        int length = (CHAT_LINE_LENGTH - head.length()) / 2;
        String line = " " + createLine(length) + ChatColor.RESET + " ";

        return (line + head + line).trim();
    }

    public static void sendTitleMessage(Sender to, String title) {
        sendTitleMessage(to, title, null);
    }

    public static void sendTitleMessage(Sender to, String title, String extra) {
        to.send(createTitle(title, extra));
    }
}
