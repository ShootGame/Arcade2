package pl.themolka.arcade.condition;

public enum OptionalResult implements AbstainableResult, ConditionResult, InvertableResult<OptionalResult> {
    TRUE {
        @Override
        public OptionalResult invert() {
            return FALSE;
        }

        @Override
        public boolean isTrue() {
            return true;
        }

        @Override
        public boolean isFalse() {
            return false;
        }

        @Override
        public boolean isAbstaining() {
            return false;
        }
    },

    FALSE {
        @Override
        public OptionalResult invert() {
            return TRUE;
        }

        @Override
        public boolean isTrue() {
            return false;
        }

        @Override
        public boolean isFalse() {
            return true;
        }

        @Override
        public boolean isAbstaining() {
            return false;
        }
    },

    ABSTAIN {
        @Override
        public OptionalResult invert() {
            return this;
        }

        @Override
        public boolean isTrue() {
            return false;
        }

        @Override
        public boolean isFalse() {
            return false;
        }

        @Override
        public boolean isAbstaining() {
            return true;
        }

        @Override
        public boolean toBoolean() {
            return false;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    };

    @Override
    public String toString() {
        return Boolean.toString(this.toBoolean());
    }

    public static OptionalResult getDefault() {
        return ABSTAIN;
    }

    public static OptionalResult fromBoolean(Boolean value) {
        return value != null ? fromBoolean(value.booleanValue()) : ABSTAIN;
    }

    public static OptionalResult fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static OptionalResult valueOf(ConditionResult result) {
        if (result instanceof AbstainableResult && ((AbstainableResult) result).isAbstaining()) {
            return ABSTAIN;
        }

        return fromBoolean(result.toBoolean());
    }
}
