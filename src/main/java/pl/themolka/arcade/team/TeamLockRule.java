package pl.themolka.arcade.team;

import pl.themolka.arcade.filter.Filter;

public class TeamLockRule {
    private final Filter filter;
    private final String message;

    public TeamLockRule() {
        this(null);
    }

    public TeamLockRule(Filter filter) {
        this(filter, null);
    }

    public TeamLockRule(Filter filter, String message) {
        this.filter = filter;
        this.message = message;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean hasFilter() {
        return this.filter != null;
    }

    public boolean hasMessage() {
        return this.message != null;
    }
}
