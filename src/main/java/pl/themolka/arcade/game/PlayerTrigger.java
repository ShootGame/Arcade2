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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PlayerTrigger implements PlayerApplicable {
    private final List<PlayerApplicable> triggers = new ArrayList<>();

    private PlayerTrigger() {
    }

    @Override
    public void apply(GamePlayer player) {
        if (player.isOnline()) {
            for (PlayerApplicable trigger : this.triggers) {
                trigger.apply(player);
            }
        }
    }

    public boolean add(PlayerApplicable trigger) {
        return this.triggers.add(trigger);
    }

    public boolean addAll(Collection<PlayerApplicable> triggers) {
        return this.triggers.addAll(triggers);
    }

    public List<PlayerApplicable> getTriggers() {
        return new ArrayList<>(this.triggers);
    }

    public boolean isEmpty() {
        return this.triggers.isEmpty();
    }

    public boolean remove(PlayerApplicable trigger) {
        return this.triggers.remove(trigger);
    }

    public boolean removeAll(Collection<PlayerApplicable> triggers) {
        return this.triggers.removeAll(triggers);
    }

    //
    // Instancing
    //

    public static PlayerTrigger create() {
        return new PlayerTrigger();
    }

    public static PlayerTrigger create(PlayerApplicable trigger) {
        PlayerTrigger value = create();
        value.add(trigger);
        return value;
    }

    public static PlayerTrigger create(Collection<PlayerApplicable> triggers) {
        PlayerTrigger value = create();
        value.addAll(triggers);
        return value;
    }

    public static PlayerTrigger create(PlayerApplicable... triggers) {
        return create(Arrays.asList(triggers));
    }
}
