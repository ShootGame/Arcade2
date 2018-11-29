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

package pl.themolka.arcade.dominator;

import java.util.Map;

public enum DefaultDominators implements Dominator<Object> {
    EVERYBODY(new Everybody()),
    EXCLUSIVE(new Exclusive()),
    LEAD(new Lead()),
    MAJORITY(new Majority()),
    NOBODY(new Nobody()),
    ;

    private final Dominator<Object> target;

    DefaultDominators(Dominator<Object> target) {
        this.target = target;
    }

    @Override
    public Map<Object, Integer> getDominators(Map<Object, Integer> input) {
        return this.target.getDominators(input);
    }

    public Dominator<Object> getDominator() {
        return this.target;
    }

    public static <T> Dominator<T> getDefault() {
        return (Dominator<T>) LEAD.target;
    }
}
