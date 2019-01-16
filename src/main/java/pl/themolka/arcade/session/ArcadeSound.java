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

package pl.themolka.arcade.session;

import org.bukkit.Sound;

public enum ArcadeSound {
    CHAT_MENTION(Sound.ENTITY_CHICKEN_EGG),
    ELIMINATION(Sound.ENTITY_IRON_GOLEM_DEATH),
    ENEMY_LOST(Sound.ENTITY_WITHER_DEATH),
    ENEMY_WON(Sound.ENTITY_WITHER_SPAWN),
    OBJECTIVE(Sound.ENTITY_WITHER_AMBIENT),
    OBJECTIVE_LOST(Sound.ENTITY_BLAZE_DEATH),
    OBJECTIVE_SCORED(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE),
    STARTED(Sound.BLOCK_ANVIL_LAND),
    STARTING(Sound.ENTITY_EXPERIENCE_ORB_PICKUP),
    TICK(Sound.BLOCK_STONE_BUTTON_CLICK_ON),
    TIME_OUT(Sound.BLOCK_PORTAL_TRIGGER),
    ;

    public static final float DEFAULT_PITCH = 1.0F;
    public static final float DEFAULT_VOLUME = 1.0F;

    private final Sound sound;

    ArcadeSound(Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return this.sound;
    }
}
