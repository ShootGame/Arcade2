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

package pl.themolka.arcade.potion;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectBuilder implements Builder<PotionEffect> {
    public static final int DURATION_INFINITY = Integer.MAX_VALUE;

    private boolean ambient = true;
    private int amplifier = 0; // effect power of I
    private int duration = DURATION_INFINITY;
    private boolean icon;
    private boolean particles = true;
    private PotionEffectType type;

    @Override
    public PotionEffect build() {
        return new PotionEffect(this.type(),
                                this.duration(),
                                this.amplifier(),
                                this.ambient(),
                                this.particles(),
                                this.icon());
    }

    public boolean ambient() {
        return this.ambient;
    }

    public PotionEffectBuilder ambient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public int amplifier() {
        return this.amplifier;
    }

    public PotionEffectBuilder amplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public int duration() {
        return this.duration;
    }

    public PotionEffectBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public boolean icon() {
        return this.icon;
    }

    public PotionEffectBuilder icon(boolean icon) {
        this.icon = icon;
        return this;
    }

    public boolean particles() {
        return this.particles;
    }

    public PotionEffectBuilder particles(boolean particles) {
        this.particles = particles;
        return this;
    }

    public PotionEffectType type() {
        return this.type;
    }

    public PotionEffectBuilder type(PotionEffectType type) {
        this.type = type;
        return this;
    }

    public static PotionEffect clone(PotionEffect original) {
        return new PotionEffect(original.getType(),
                                original.getDuration(),
                                original.getAmplifier(),
                                original.isAmbient(),
                                original.hasParticles(),
                                original.hasIcon());
    }
}
