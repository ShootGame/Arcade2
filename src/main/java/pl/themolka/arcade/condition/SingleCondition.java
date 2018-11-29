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

public abstract class SingleCondition<K, V extends ConditionResult> implements Condition<K, V> {
    protected final Condition<K, V> condition;

    public SingleCondition(Condition<K, V> condition) {
        this.condition = condition;
    }

    @Override
    public V query(K k) {
        return this.query(k, this.condition);
    }

    public Condition<K, V> getCondition() {
        return this.condition;
    }

    public abstract V query(K k, Condition<K, V> condition);
}
