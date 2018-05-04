package pl.themolka.arcade.condition;

public enum BooleanResult implements ConditionResult, InvertableResult<BooleanResult> {
    TRUE(true) {
        @Override
        public BooleanResult invert() {
            return FALSE;
        }
    },

    FALSE(false) {
        @Override
        public BooleanResult invert() {
            return TRUE;
        }
    };

    private final boolean value;

    BooleanResult(boolean value) {
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

    public static BooleanResult fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static BooleanResult valueOf(ConditionResult result) {
        return fromBoolean(result.toBoolean());
    }
}
