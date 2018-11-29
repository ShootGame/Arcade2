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

package pl.themolka.arcade.game;

/**
 * Filtering players visibility by other players.
 */
public interface PlayerVisibilityFilter {
    /**
     * The default filter which always show other players.
     */
    PlayerVisibilityFilter DEFAULT = new DefaultVisibilityFilter();

    /**
     * Check if a <code>viewer</code> can see <code>target</code>.
     * @param viewer Viewer who can or cannot see the <code>target</code> player.
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return <code>true</code> if <code>target</code> is visible, <code>false</code> otherwise.
     */
    boolean canSee(GamePlayer viewer, GamePlayer target);
}

/**
 * The default implementation of {@link PlayerVisibilityFilter}, which
 * generally filters noting. The #canSee method returns <code>true</code>.
 */
final class DefaultVisibilityFilter implements PlayerVisibilityFilter {
    /**
     * The default implementation of {@link PlayerVisibilityFilter#canSee(GamePlayer, GamePlayer)}.
     * @param viewer Viewer who can or cannot see the <code>target</code> player.
     * @param target Player who can or cannot be viewed by the <code>viewer</code>.
     * @return always <code>target</code>.
     */
    @Override
    public boolean canSee(GamePlayer viewer, GamePlayer target) {
        return true;
    }
}
