package pl.themolka.arcade.filter;

import pl.themolka.arcade.util.Nulls;

public final class Filters {
    private Filters() {
    }

    public static Filter secure(Filter filter) {
        return Nulls.defaults(filter, undefined());
    }

    public static Filter undefined() {
        return StaticFilter.ABSTAIN;
    }
}
