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

package pl.themolka.arcade.map;

import org.bukkit.ChatColor;

import java.util.Objects;
import java.util.UUID;

public class Author implements Comparable<Author> {
    private final UUID uuid;
    private final UUID offlineUuid;
    private final String username;
    private final String description;

    private Author(UUID uuid, String username, String description) {
        Objects.requireNonNull(username, "username cannot be null");

        this.uuid = uuid;
        this.offlineUuid = this.newOfflineUuid(username);
        this.username = username;
        this.description = description;
    }

    @Override
    public int compareTo(Author o) {
        return this.getUsername().compareToIgnoreCase(o.getUsername());
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getOfflineUuid() {
        return this.offlineUuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasUuid() {
        return this.uuid != null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public boolean hasUsername() {
        return this.username != null;
    }

    @Override
    public String toString() {
        if (this.hasUsername()) {
            String author = ChatColor.GOLD +
                    this.getUsername() + ChatColor.RESET;
            if (!this.hasDescription()) {
                return author;
            }

            return author + ChatColor.GRAY + " - " + ChatColor.ITALIC +
                    this.getDescription() + ChatColor.RESET;
        }

        return null;
    }

    private UUID newOfflineUuid(String username) {
        if (username != null) {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
        }

        return null;
    }

    //
    // Instancing
    //

    public static Author of(String username) {
        return of(username, null);
    }

    public static Author of(String username, String description) {
        return of(null, username, description);
    }

    public static Author of(UUID uuid, String username) {
        return of(uuid, username, null);
    }

    public static Author of(UUID uuid, String username, String description) {
        return new Author(uuid, username, description);
    }

    public static Author plain(String uuid, String username) {
        return plain(uuid, username, null);
    }

    public static Author plain(String uuid, String username, String description) {
        return of(UUID.fromString(uuid), username, description);
    }
}
