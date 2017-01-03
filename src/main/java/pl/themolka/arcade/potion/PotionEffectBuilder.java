package pl.themolka.arcade.potion;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectBuilder implements Builder<PotionEffect> {
    private boolean ambient;
    private int amplifier;
    private Color color;
    private int duration;
    private boolean particles;
    private PotionEffectType type;

    @Override
    public PotionEffect build() {
        return new PotionEffect(
                this.type(),
                this.duration(),
                this.amplifier(),
                this.ambient(),
                this.particles(),
                this.color());
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

    public Color color() {
        return this.color;
    }

    public PotionEffectBuilder color(Color color) {
        this.color = color;
        return this;
    }

    public int duration() {
        return this.duration;
    }

    public PotionEffectBuilder duration(int duration) {
        this.duration = duration;
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
}
