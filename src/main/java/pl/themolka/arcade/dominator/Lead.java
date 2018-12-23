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
                // All participants in this map have same player counts - break the loop.
                previousPower = previous.getValue();
                break;
            }

            if (power < previousPower) {
                // Not enough participants, continue
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
