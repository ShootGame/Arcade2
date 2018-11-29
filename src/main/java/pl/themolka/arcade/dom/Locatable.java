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

package pl.themolka.arcade.dom;

import java.util.Objects;

/**
 * Something that can be located (and selected).
 */
public interface Locatable extends Selectable {
    default void locate(Cursor start, Cursor end) {
        this.locate(Selection.between(start, end));
    }

    default void locate(Selection selection) {
        Objects.requireNonNull(selection, "selection cannot be null");
        this.locate(selection.getStart(), selection.getEnd());
    }
}
