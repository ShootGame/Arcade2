package pl.themolka.arcade.filter;

public enum FilterResult {
    ALLOW(true),
    DENY(false),
    ABSTAIN(true);

    private final boolean value;

    FilterResult(boolean value) {
        this.value = value;
    }

    public FilterResult getOpposite() {
        switch (this) {
            case ALLOW: return DENY;
            case DENY: return ALLOW;
            default: return ABSTAIN;
        }
    }

    public boolean isDenied() {
        switch (this) {
            case ALLOW: return false;
            case DENY: return true;
            default: return false;
        }
    }

    public boolean isNotDenied() {
        return !this.isDenied();
    }

    public boolean toBoolean() {
        return this.value;
    }

    public static FilterResult fromBoolean(boolean value) {
        if (value) {
            return ALLOW;
        } else {
            return DENY;
        }
    }

    public static FilterResult of(boolean matches) {
        return fromBoolean(matches);
    }
}
