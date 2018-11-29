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
