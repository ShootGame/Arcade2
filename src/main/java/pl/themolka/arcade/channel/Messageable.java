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

public interface Messageable {
    ChatColor ERROR_COLOR = ChatColor.RED;
    ChatColor INFO_COLOR = ChatColor.GRAY;
    ChatColor SUCCESS_COLOR = ChatColor.GREEN;
    ChatColor TIP_COLOR = ChatColor.AQUA;

    void send(String message);

    void sendAction(String action);

    void sendChat(String chat);

    default void sendError(String error) {
        this.send(ERROR_COLOR + error);
    }

    default void sendInfo(String info) {
        this.send(INFO_COLOR + info);
    }

    default void sendSuccess(String success) {
        this.send(SUCCESS_COLOR + success);
    }

    default void sendTip(String tip) {
        this.send(TIP_COLOR + ChatColor.BOLD.toString() + "[Tip] " + ChatColor.RESET + INFO_COLOR + ChatColor.ITALIC + tip);
    }
}
