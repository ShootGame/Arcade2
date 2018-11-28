package pl.themolka.arcade.dominator;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * All object(s) dominates.
 */
public class Everybody<T> extends AbstractDominator<T> {
    @Override
    public Map<T, Integer> getDominators(Map<T, Integer> input) {
        return ImmutableMap.copyOf(input);
    }
}
