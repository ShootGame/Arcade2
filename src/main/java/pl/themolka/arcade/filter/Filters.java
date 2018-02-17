package pl.themolka.arcade.filter;

public final class Filters {
    private static final Filter undefined = StaticFilter.ABSTAIN;

    private Filters() {
    }

    public static Filter secure(Filter filter) {
        return filter != null ? filter : undefined();
    }

    public static Filter undefined() {
        return undefined;
    }
}
