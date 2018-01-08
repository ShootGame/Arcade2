package pl.themolka.arcade.filter;

public class Filters {
    private static final Filter undefined = StaticFilter.ABSTAIN;

    private Filters() {
    }

    public static Filter undefined() {
        return new Filter() {
            @Override
            public FilterResult filter(Object... objects) {
                return undefined.filter(objects);
            }
        };
    }
}
