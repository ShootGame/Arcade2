package pl.themolka.arcade.dom;

import java.util.Objects;

/**
 * Immutable selection between two {@link Cursor}s.
 */
public final class Selection {
    private final Cursor start;
    private final Cursor end;

    private Selection(Cursor start, Cursor end) {
        start = Objects.requireNonNull(start, "start cannot be null");
        end = Objects.requireNonNull(end, "end cannot be null");

        // Invert start and end if end is before start.
        if (start.isAfter(end)) {
            this.start = end;
            this.end = start;
        } else {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Calculate horizontal length between defined {@link Cursor}s.
     */
    public int distance() {
        if (this.start.isSameLine(this.end)) {
            return this.end.getColumn() - this.start.getColumn();
        }

        throw new IllegalStateException("Cannot calculate distance between different lines.");
    }

    public Selection expandLeft(int left) {
        return between(this.start.moveLeft(left), this.end);
    }

    public Selection expandRight(int right) {
        return between(this.start, this.end.moveRight(right));
    }

    public Cursor getStart() {
        return this.start;
    }

    public int getStartLine() {
        return this.start.getLine();
    }

    public int getStartColumn() {
        return this.start.getColumn();
    }

    public Cursor getEnd() {
        return this.end;
    }

    public int getEndLine() {
        return this.end.getLine();
    }

    public int getEndColumn() {
        return this.end.getColumn();
    }

    public boolean isEmpty() {
        return this.start.equals(this.end);
    }

    public boolean isMultiline() {
        return !this.start.isSameLine(this.end);
    }

    /**
     * Calculate vertical length between defined {@link Cursor}s.
     */
    public int length() {
        if (this.start.isSameLine(this.end)) {
            return 0;
        }

        return this.getEndLine() - this.getStartLine() - this.getEndColumn() != 0 ? 1 : 0;
    }

    /**
     * Count total amount of lines.
     */
    public int lines() {
        return this.length() + 1;
    }

    public Selection shiftLeft(int left) {
        Cursor newEnd = this.end.moveLeft(left);
        if (newEnd.isAfter(this.start)) {
            throw new IllegalArgumentException("Before the starting cursor.");
        }

        return between(this.start, newEnd);
    }

    public Selection shiftRight(int right) {
        Cursor newStart = this.start.moveRight(right);
        if (newStart.isAfter(this.end)) {
            throw new IllegalArgumentException("After the ending cursor.");
        }

        return between(newStart, this.end);
    }

    //
    // Instancing
    //

    /**
     * Create a {@link Selection} between two {@link Cursor}s.
     */
    public static Selection between(Cursor start, Cursor end) {
        return new Selection(start, end);
    }

    /**
     * Create a {@link Selection} between two points.
     */
    public static Selection between(int startLine, int startColumn, int endLine, int endColumn) {
        return between(new Cursor(Math.min(startLine, endLine), Math.min(startColumn, endColumn)),
                       new Cursor(Math.max(startLine, endLine), Math.max(startColumn, endColumn)));
    }

    /**
     * Create a {@link Selection} between start and end lines ignoring columns.
     */
    public static Selection block(int start, int end) {
        // Add 1 to the end so the last line is selected too (the column is 0).
        return between(Math.min(start, end), 0,
                       Math.max(start, end) + 1, 0);
    }

    /**
     * Create a {@link Selection} based on the given {@link Cursor}.
     */
    public static Selection cursor(Cursor cursor) {
        return between(cursor, cursor);
    }

    /**
     * Create a {@link Selection} based on the given position.
     */
    public static Selection cursor(int line, int column) {
        return cursor(new Cursor(line, column));
    }

    /**
     * Create a {@link Selection} between two columns on the line.
     */
    public static Selection inline(int line, int start, int end) {
        return between(line, Math.min(start, end), line, Math.max(start, end));
    }
}
