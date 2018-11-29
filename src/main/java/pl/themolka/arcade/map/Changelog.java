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

import pl.themolka.arcade.util.versioning.Version;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Changelog<T extends Version<T>> extends ArrayList<String> {
    private final T version;
    private LocalDate release;

    public Changelog(T version) {
        this.version = version;
    }

    public Changelog(T version, LocalDate release) {
        this(version);

        this.release = release;
    }

    public T getVersion() {
        return this.version;
    }

    public LocalDate getRelease() {
        return this.release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }

    public static LocalDate parseRelease(String query) throws DateTimeParseException {
        if (query != null) {
            return LocalDate.parse(query);
        }

        return null;
    }
}
