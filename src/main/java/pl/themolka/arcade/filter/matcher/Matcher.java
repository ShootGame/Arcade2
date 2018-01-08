package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.filter.AbstractFilter;
import pl.themolka.arcade.filter.FilterResult;

public abstract class Matcher extends AbstractFilter {
    @Override
    public FilterResult filter(Object object) {
        return this.matches(object);
    }

    protected FilterResult abstain() {
        return FilterResult.ABSTAIN;
    }

    protected FilterResult allow() {
        return FilterResult.ALLOW;
    }

    protected FilterResult deny() {
        return FilterResult.DENY;
    }

    public abstract FilterResult matches(Object object);

    protected FilterResult of(boolean result) {
        return FilterResult.of(result);
    }
}
