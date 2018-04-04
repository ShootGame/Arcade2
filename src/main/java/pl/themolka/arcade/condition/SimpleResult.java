package pl.themolka.arcade.condition;

public enum SimpleResult implements ConditionResult, InvertableResult<SimpleResult> {
    TRUE(true) {
        @Override
        public SimpleResult invert() {
            return FALSE;
        }
    },

    FALSE(false) {
        @Override
        public SimpleResult invert() {
            return TRUE;
        }
    };

    private final boolean value;

    SimpleResult(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isTrue() {
        return this.value;
    }

    @Override
    public boolean isNotTrue() {
        return this.isFalse();
    }

    @Override
    public boolean isFalse() {
        return !this.value;
    }

    @Override
    public boolean isNotFalse() {
        return this.isTrue();
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

    //
    // Instancing
    //

    public static SimpleResult fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static SimpleResult valueOf(ConditionResult result) {
        return fromBoolean(result.toBoolean());
    }
}
