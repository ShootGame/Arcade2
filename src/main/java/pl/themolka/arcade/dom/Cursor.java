package pl.themolka.arcade.dom;

import java.util.Objects;

public class Cursor {
    private final int line;
    private final int column;

    public Cursor(int line, int column) {
        this.line = line;
        this.column = column;
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
}
