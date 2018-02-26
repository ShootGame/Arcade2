package pl.themolka.arcade.dom;

import java.util.Objects;

/**
 * Immutable cursor position in a document.
 */
public class Cursor {
    private final int line;
    private final int column;

    public Cursor(int line, int column) {
        this.line = line < 0 ? 0 : line;
        this.column = column < 0 ? 0 : column;
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
