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

package pl.themolka.arcade.match;

import java.util.List;

/**
 * An interface accessing modules to fetch current - dynamically
 * {@link MatchWinner}s at any time.
 */
public interface DynamicWinnable {
    /**
     * Current, not cached {@link MatchWinner} in this {@link Match}.
     * @return current {@link MatchWinner} if one is winning,
     * <code>null</code> if not, {@link DrawMatchWinner} if many.
     */
    default MatchWinner getDynamicWinner() {
        List<MatchWinner> winners = this.getDynamicWinners();
        if (winners == null || winners.isEmpty()) {
            // no one is winning
            return null;
        } else if (winners.size() == 1) {
            // there is only a one winner, so we can return it
            return winners.get(0);
        } else {
            // there are many winners, return a multi instead
            return MultiMatchWinner.of(winners);
        }
    }

    /**
     * Current list of {@link MatchWinner}s in this {@link Match}.
     * @return {@link List} of current {@link MatchWinner}s.
     * <code>null</code> if no one is currently winning.
     */
    List<MatchWinner> getDynamicWinners();
}
