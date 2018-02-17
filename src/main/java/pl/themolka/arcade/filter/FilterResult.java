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

    public boolean isAllowed() {
        return this == ALLOW;
    }

    public boolean isDenied() {
        return this == DENY;
    }

    public boolean isNotDenied() {
        return this != DENY;
    }

    public boolean toBoolean() {
        return this.value;
    }

    public static FilterResult fromBoolean(boolean value) {
        return value ? ALLOW : DENY;
    }

    public static FilterResult of(boolean matches) {
        return fromBoolean(matches);
    }
}
