package pl.themolka.arcade.filter;

public class StaticFilter extends AbstractFilter {
    public static final StaticFilter ABSTAIN = new StaticFilter(FilterResult.ABSTAIN);
    public static final StaticFilter ALLOW = new StaticFilter(FilterResult.ALLOW);
    public static final StaticFilter DENY = new StaticFilter(FilterResult.DENY);

    private final FilterResult result;

    public StaticFilter(FilterResult result) {
        this.result = result;
    }

    @Override
    public FilterResult filter(Object object) {
        return this.getResult();
    }

    public FilterResult getResult() {
        return this.result;
    }
}
