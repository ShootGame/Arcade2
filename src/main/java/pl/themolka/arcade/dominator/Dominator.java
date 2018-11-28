package pl.themolka.arcade.dominator;

import java.util.Map;

/**
 * Base class for all domination strategies.
 * @param <T> type to filter
 */
public interface Dominator<T> {
    Map<T, Integer> getDominators(Map<T, Integer> input);
}
