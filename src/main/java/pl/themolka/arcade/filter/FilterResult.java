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

    public boolean isNotDenied() {
        switch (this){
            case ALLOW: return true;
            case DENY: return false;
            default: return true;
        }
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
