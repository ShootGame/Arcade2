package pl.themolka.arcade.util.pagination;

public interface Paginationable extends Comparable<Paginationable> {
    @Override
    default int compareTo(Paginationable o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

    String paginate(int index);
}
