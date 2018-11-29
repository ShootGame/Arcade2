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

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

public abstract class MultiCondition<K> implements Condition<K, AbstainableResult> {
    protected final List<Condition<K, AbstainableResult>> conditions;

    public MultiCondition(Collection<Condition<K, AbstainableResult>> conditions) {
        this.conditions = ImmutableList.copyOf(conditions);
    }

    @Override
    public AbstainableResult query(K k) {
        if (k != null) {
            for (Condition<K, AbstainableResult> condition : this.conditions) {
                AbstainableResult result = this.query(k, condition);

                if (result.isNotAbstaining()) {
                    return result;
                }
            }
        }

        return this.defaultValue();
    }

    public AbstainableResult query(K[] k) {
        if (k != null) {
            for (K item : k) {
                AbstainableResult result = this.query(item);

                if (result.isNotAbstaining()) {
                    return result;
                }
            }
        }

        return OptionalResult.ABSTAIN;
    }

    public abstract AbstainableResult defaultValue();

    public List<Condition<K, AbstainableResult>> getConditions() {
        return this.conditions;
    }

    public abstract AbstainableResult query(K k, Condition<K, AbstainableResult> condition);
}
