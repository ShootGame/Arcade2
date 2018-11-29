/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
