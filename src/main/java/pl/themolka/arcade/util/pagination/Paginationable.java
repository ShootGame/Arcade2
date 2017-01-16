package pl.themolka.arcade.util.pagination;

public interface Paginationable extends Comparable<Paginationable> {
    String paginate(int index);
}
