package pl.themolka.arcade.dominator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Objects(s) with the highest power dominates.
 */
public class Lead<T> extends AbstractDominator<T> {
    @Override
    public Map<T, Integer> getDominators(Map<T, Integer> input) {
        Map<T, Integer> results = new LinkedHashMap<>();
        for (Map.Entry<T, Integer> dominator : input.entrySet()) {
            int power = dominator.getValue();

            int previousPower = 0;
            for (Map.Entry<T, Integer> previous : results.entrySet()) {
                // All participators in this map have same player counts - break the loop.
                previousPower = previous.getValue();
                break;
            }

            if (power < previousPower) {
                // Not enough participators, continue
                continue;
            } else if (power > previousPower) {
                // Clear the map so we have just this record in it.
                results.clear();
            }

            // Record the current participator to the map.
            results.put(dominator.getKey(), power);
        }

        return results;
    }
}
