package pl.themolka.arcade.filter;

public class Filters {
    private static final Filter undefined = StaticFilter.ABSTAIN;

    private Filters() {
    }

    public static Filter undefined() {
        return undefined;
    }
}
