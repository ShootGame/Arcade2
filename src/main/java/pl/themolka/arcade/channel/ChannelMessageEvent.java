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

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.event.Cancelable;

public class ChannelMessageEvent extends ChannelEvent implements Cancelable {
    private final Sender author;
    private String authorName;
    private boolean cancel;
    private String message;

    public ChannelMessageEvent(ArcadePlugin plugin,
                               Channel channel,
                               Sender author,
                               String authorName,
                               String message) {
        super(plugin, channel);

        this.author = author;
        this.authorName = authorName;
        this.message = message;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Sender getAuthor() {
        return this.author;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
