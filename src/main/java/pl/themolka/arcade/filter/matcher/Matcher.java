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

package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.OptionalResult;
import pl.themolka.arcade.filter.Filter;

public abstract class Matcher<T> implements Filter {
    @Override
    public final AbstainableResult filter(Object... objects) {
        if (objects != null) {
            for (Object object : objects) {
                if (this.find(object)) {
                    return OptionalResult.TRUE;
                }
            }
        }

        return OptionalResult.ABSTAIN;
    }

    public abstract boolean find(Object object);

    public abstract boolean matches(T t);

    interface Config<T extends Matcher<?>> extends Filter.Config<T> {
    }
}
