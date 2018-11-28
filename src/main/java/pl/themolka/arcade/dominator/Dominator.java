package pl.themolka.arcade.dominator;

import java.util.Map;

/**
 * Base class for all domination strategies.
 * @param <T> type to filter
 */
public interface Dominator<T> {
    /**
     * Filter given T objects (aka dominators) by their power.
     * @param input objects to filter
     * @return an immutable map with current dominators from the given map
     */
    Map<T, Integer> getDominators(Map<T, Integer> input);
}
