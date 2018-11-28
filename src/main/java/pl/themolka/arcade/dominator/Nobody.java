package pl.themolka.arcade.dominator;

import java.util.Map;

/**
 * Nobody dominates.
 */
public class Nobody<T> extends AbstractDominator<T> {
    @Override
    public Map<T, Integer> getDominators(Map<T, Integer> input) {
        return this.empty();
    }
}
