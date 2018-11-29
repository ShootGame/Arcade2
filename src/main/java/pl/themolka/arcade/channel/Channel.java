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

import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.util.StringId;

import java.util.List;

public interface Channel extends Messageable, StringId {
    boolean addMember(Sender member);

    List<Sender> getMembers();

    String getPermission();

    boolean hasMember(Sender member);

    boolean removeMember(Sender member);

    int sendMessage(String message);

    int sendActionMessage(String action);

    int sendChatMessage(String chat);

    int sendChatMessage(Sender author, String chat);

    int sendErrorMessage(String error);

    int sendInfoMessage(String info);

    int sendSuccessMessage(String success);

    int sendTipMessage(String tip);
}
