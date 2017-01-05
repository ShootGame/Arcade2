package pl.themolka.arcade.filter;

public enum FilterResult {
    TRUE(true),
    FALSE(false),
    ABSTAIN(true);

    private final boolean value;

    FilterResult(boolean value) {
        this.value = value;
    }

    public boolean toBoolean() {
        return this.value;
    }

    public static FilterResult fromBoolean(boolean value) {
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}
