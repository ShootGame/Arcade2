package pl.themolka.arcade.dominator;

import java.util.Map;

/**
 * Only one object dominates.
 */
public class Exclusive<T> extends AbstractDominator<T> {
    @Override
    public Map<T, Integer> getDominators(Map<T, Integer> input) {
        return input.keySet().size() == 1 ? input : this.empty();
    }
}
