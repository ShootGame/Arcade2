package pl.themolka.arcade.dominator;

import java.util.Map;

/**
 * All object(s) dominates.
 */
public class Everybody<T> extends AbstractDominator<T> {
    @Override
    public Map<T, Integer> getDominators(Map<T, Integer> input) {
        return input;
    }
}
