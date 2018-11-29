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
 * Immutable cursor position in a document.
 */
public class Cursor {
    private final int line;
    private final int column;

    public Cursor(int line, int column) {
        this.line = Math.max(1, line);
        this.column = Math.max(1, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cursor) {
            Cursor that = (Cursor) obj;
            return this.line == that.line &&
                   this.column == that.column;
        }

        return false;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.line, this.column);
    }

    /**
     * Is THIS after the given target?
     */
    public boolean isAfter(Cursor target) {
        return this.line > target.line || (this.line == target.line && this.column > target.column);
    }

    /**
     * Is THIS before the given target?
     */
    public boolean isBefore(Cursor target) {
        return this.line < target.line || (this.line == target.line && this.column < target.column);
    }

    public boolean isSameLine(Cursor target) {
        return this.line == target.line;
    }

    public Cursor move(int lines, int columns) {
        return new Cursor(this.line + lines, this.column + columns);
    }

    public Cursor moveDown(int down) {
        return this.move(+down, -this.column);
    }

    public Cursor moveDownRelative(int down) {
        return this.move(+down, 0);
    }

    public Cursor moveLeft(int left) {
        return this.move(0, -left);
    }

    public Cursor moveRight(int right) {
        return this.move(0, +right);
    }

    public Cursor moveUp(int up) {
        return this.move(-up, -this.column);
    }

    public Cursor moveUpRelative(int up) {
        return this.move(-up, 0);
    }

    @Override
    public String toString() {
        return "L" + this.line + ":" + this.column;
    }
}
