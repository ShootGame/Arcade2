package pl.themolka.arcade.filter;

public interface Filter {
    default FilterResult filter(Object... objects) {
        return FilterResult.ABSTAIN;
    }
}
