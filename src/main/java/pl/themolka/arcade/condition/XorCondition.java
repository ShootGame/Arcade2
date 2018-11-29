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

public class XorCondition<K> extends MultiCondition<K> {
    public XorCondition(Collection<Condition<K, AbstainableResult>> xor) {
        super(xor);
    }

    @Override
    public AbstainableResult query(K k) {
        ConditionResult last = null;
        for (Condition<K, AbstainableResult> condition : this.conditions) {
            AbstainableResult result = condition.query(k);
            if (result.isAbstaining()) {
                continue;
            }

            if (last != null && !last.equals(result)) {
                return OptionalResult.FALSE;
            }
            last = result;
        }

        return OptionalResult.TRUE;
    }

    @Override
    public String toString() {
        return "XOR(" + StringUtils.join(this.conditions, ", ") + ")";
    }

    //
    // Unused Methods
    //

    @Override
    public final AbstainableResult defaultValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final AbstainableResult query(K k, Condition<K, AbstainableResult> condition) {
        throw new UnsupportedOperationException();
    }
}
