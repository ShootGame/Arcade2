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

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class OrCondition<K> extends MultiCondition<K> {
    public OrCondition(Collection<Condition<K, AbstainableResult>> or) {
        super(or);
    }

    @Override
    public AbstainableResult defaultValue() {
        return OptionalResult.FALSE;
    }

    @Override
    public AbstainableResult query(K k, Condition<K, AbstainableResult> condition) {
        return condition.query(k).isTrue() ? OptionalResult.TRUE
                                           : OptionalResult.ABSTAIN;
    }

    @Override
    public String toString() {
        return "OR(" + StringUtils.join(this.conditions, ", ") + ")";
    }
}
