package pl.themolka.arcade.dominator;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractDominator<T> implements Dominator<T> {
    protected Map<T, Integer> empty() {
        return Collections.EMPTY_MAP;
    }

    protected Map<T, Integer> singleton(Map.Entry<T, Integer> dominator) {
        return this.singleton(dominator.getKey(), dominator.getValue());
    }

    protected Map<T, Integer> singleton(T t, Integer power) {
        return ImmutableMap.of(t, power);
    }
}
