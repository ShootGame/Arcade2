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

import java.util.Arrays;
import java.util.Collection;

public final class Query {
    private Query() {
    }

    public static <T> AndCondition<T> and(Collection<Condition<T, AbstainableResult>> and) {
        return new AndCondition<>(and);
    }

    @SafeVarargs
    public static <T> AndCondition<T> and(Condition<T, AbstainableResult>... and) {
        return and(toCollection(and));
    }

    public static <T> OrCondition<T> or(Collection<Condition<T, AbstainableResult>> or) {
        return new OrCondition<>(or);
    }

    @SafeVarargs
    public static <T> OrCondition<T> or(Condition<T, AbstainableResult>... or) {
        return or(toCollection(or));
    }

    public static <T, V extends InvertableResult<V>> NandCondition<T, V> nand(Collection<Condition<T, V>> nand) {
        return new NandCondition<>(nand);
    }

    @SafeVarargs
    public static <T, V extends InvertableResult<V>> NandCondition<T, V> nand(Condition<T, V>... nand) {
        return nand(toCollection(nand));
    }

    public static <T> NoneCondition<T> none(Collection<Condition<T, AbstainableResult>> none) {
        return new NoneCondition<>(none);
    }

    @SafeVarargs
    public static <T> NoneCondition<T> none(Condition<T, AbstainableResult>... none) {
        return none(toCollection(none));
    }

    public static <T, V extends InvertableResult<V>> NorCondition<T, V> nor(Collection<Condition<T, V>> nor) {
        return new NorCondition<>(nor);
    }

    @SafeVarargs
    public static <T, V extends InvertableResult<V>> NorCondition<T, V> nor(Condition<T, V>... nor) {
        return nor(toCollection(nor));
    }

    public static <T, V extends InvertableResult<V>> NotCondition<T, V> not(Condition<T, V> not) {
        return new NotCondition<>(not);
    }

    public static <T, V extends InvertableResult<V>> XnorCondition<T, V> xnor(Collection<Condition<T, V>> xnor) {
        return new XnorCondition<>(xnor);
    }

    @SafeVarargs
    public static <T, V extends InvertableResult<V>> XnorCondition<T, V> xnor(Condition<T, V>... xnor) {
        return xnor(toCollection(xnor));
    }

    public static <T> XorCondition<T> xor(Collection<Condition<T, AbstainableResult>> xor) {
        return new XorCondition<>(xor);
    }

    @SafeVarargs
    public static <T> XorCondition<T> xor(Condition<T, AbstainableResult>... xor) {
        return xor(toCollection(xor));
    }

    static <T extends Condition<?, ?>> Collection<T> toCollection(T[] t) {
        return Arrays.asList(t);
    }
}
